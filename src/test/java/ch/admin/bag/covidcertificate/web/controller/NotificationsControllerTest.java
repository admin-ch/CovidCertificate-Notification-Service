package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.Constants;
import ch.admin.bag.covidcertificate.api.request.MessageDto;
import ch.admin.bag.covidcertificate.api.request.MessageType;
import ch.admin.bag.covidcertificate.api.request.NotificationDto;
import ch.admin.bag.covidcertificate.config.security.OAuth2SecuredWebConfiguration;
import ch.admin.bag.covidcertificate.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flextrade.jfixture.JFixture;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = {NotificationsController.class})
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(roles = {"bag-cc-superuser"})
@DisplayName("Tests for NotificationsController")
class NotificationsControllerTest {
    @MockBean
    private NotificationService notificationService;
    @MockBean
    private OAuth2SecuredWebConfiguration.OAuth2SecuredWebMvcConfiguration oAuth2SecuredWebMvcConfiguration;
    @MockBean
    private SecurityHelper securityHelper;
    @Autowired
    private MockMvc mockMvc;

    private static final String URL = "/api/v1/notifications";
    private static final JFixture fixture = new JFixture();
    private final ObjectMapper mapper = new ObjectMapper();
    private NotificationDto validNotificationDto;

    @BeforeEach
    void beforeEach() {
        when(securityHelper.authorizeUser(any())).thenReturn(true);
        this.validNotificationDto = new NotificationDto(
                MessageType.INFO,
                new MessageDto("de", "fr", "it", "en"),
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(1)
        );
    }

    @Nested
    @DisplayName("Tests for get")
    class GetAllNotificationsTest {
        @Test
        @DisplayName("When called, it should authorize the user")
        void GetTest1() throws Exception {
            // given
            List<NotificationDto> notifications = Collections.emptyList();
            when(notificationService.readNotifications()).thenReturn(notifications);

            // when
            mockMvc.perform(get(URL)
                    .accept(MediaType.ALL_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", fixture.create(String.class)));

            // then
            verify(securityHelper).authorizeUser(any());
        }

        @Test
        @DisplayName("Given no notifications are present, when called, it should return 204 No Content")
        void GetTest2() throws Exception {
            // given
            List<NotificationDto> notifications = Collections.emptyList();

            // when
            when(notificationService.readNotifications()).thenReturn(notifications);

            // then
            mockMvc.perform(get(URL)
                            .accept(MediaType.ALL_VALUE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", fixture.create(String.class)))
                    .andExpect(status().isNoContent());

        }

        @Test
        @DisplayName("Given notifications are present, when called, it should return 200 OK")
        void GetTest3() throws Exception {
            // given
            var notifications = List.of(new NotificationDto());

            // when
            when(notificationService.readNotifications()).thenReturn(notifications);

            // then
            mockMvc.perform(get(URL)
                            .accept(MediaType.ALL_VALUE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", fixture.create(String.class)))
                    .andExpect(status().isOk());

        }

        @Test
        @DisplayName("Given notifications are present, when called, it should return the notifications")
        void GetTest4() throws Exception {
            // given
            var notifications = List.of(new NotificationDto());

            // when
            when(notificationService.readNotifications()).thenReturn(notifications);

            // then
            mockMvc.perform(get(URL)
                            .accept(MediaType.ALL_VALUE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", fixture.create(String.class)))
                    .andExpect(content().json(mapper.writeValueAsString(notifications)));
        }
    }

    @Nested
    @DisplayName("Tests for post")
    class WriteNotificationsTest {

        @BeforeEach
        void beforeEach() {
            lenient().doNothing().when(notificationService).writeNotifications(any());
        }

        @Test
        @DisplayName("When called, it should authorize the user")
        void PostTest1() throws Exception {
            // given
            var notifications = List.of(validNotificationDto);
            var notificationsStr = mapper.writeValueAsString(notifications);

            // when
            mockMvc.perform(post(URL)
                    .accept(MediaType.ALL_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", fixture.create(String.class))
                    .content(notificationsStr));

            // then
            verify(securityHelper).authorizeUser(any());
        }

        @Test
        @DisplayName("Given notifications are valid, when called, it should return status 201 Created")
        void PostTest2() throws Exception {
            // given
            var notifications = List.of(validNotificationDto);
            var notificationsStr = mapper.writeValueAsString(notifications);

            // when then
            mockMvc.perform(post(URL)
                            .accept(MediaType.ALL_VALUE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", fixture.create(String.class))
                            .content(notificationsStr))
                    .andExpect(status().isCreated());

            verify(notificationService).writeNotifications(any());
        }

        @ParameterizedTest
        @CsvFileSource(resources = "/csv/notifications_dto.csv", delimiter = 'þ')
        @DisplayName("Given notifications are invalid, when called, it should return status 400 Bad Request")
        void PostTest3(String notifications, String expectedErrMsg) throws Exception {
            // given
            var request = post(URL)
                    .accept(MediaType.ALL_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", fixture.create(String.class));

            if (Objects.nonNull(notifications)) {
                request.content(notifications);
            }

            // when then
            mockMvc.perform(request)
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(Matchers.containsString(expectedErrMsg)));

        }

        @Test
        @DisplayName("Given 'end' is in past, when called, it should return status 400 Bad Request")
        void PostTest4() throws Exception {
            validNotificationDto.setEnd(LocalDateTime.now().minusHours(1));
            validNotificationDto.setStart(LocalDateTime.now().minusHours(2));
            var notificationsStr = mapper.writeValueAsString(List.of(validNotificationDto));

            // given
            var request = post(URL)
                    .accept(MediaType.ALL_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(notificationsStr)
                    .header("Authorization", fixture.create(String.class));

            // when then
            mockMvc.perform(request)
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(Matchers.containsString(Constants.END_MUST_NOT_BE_IN_PAST)));

        }

    }

    @Nested
    @DisplayName("Tests for delete")
    class RemoveNotificationsTest {

        @BeforeEach
        void beforeEach() {
            lenient().doNothing().when(notificationService).removeNotifications();
        }

        @Test
        @DisplayName("When called, it should authorize the user")
        void DeleteTest1() throws Exception {
            // given
            var notifications = List.of(validNotificationDto);
            var notificationsStr = mapper.writeValueAsString(notifications);

            // when
            mockMvc.perform(delete(URL)
                    .accept(MediaType.ALL_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", fixture.create(String.class)));

            // then
            verify(securityHelper).authorizeUser(any());
        }

        @Test
        @DisplayName("Given notifications are valid, when called, it should return 201 Created")
        void DeleteTest2() throws Exception {
            // when then
            mockMvc.perform(delete(URL)
                            .accept(MediaType.ALL_VALUE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", fixture.create(String.class)))
                    .andExpect(status().isNoContent());

            verify(notificationService).removeNotifications();
        }
    }
}