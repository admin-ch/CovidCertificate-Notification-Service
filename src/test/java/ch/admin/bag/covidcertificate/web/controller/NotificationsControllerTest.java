package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.Constants;
import ch.admin.bag.covidcertificate.api.request.CreateNotificationDto;
import ch.admin.bag.covidcertificate.api.request.EditNotificationDto;
import ch.admin.bag.covidcertificate.api.request.NotificationContentDto;
import ch.admin.bag.covidcertificate.api.request.NotificationType;
import ch.admin.bag.covidcertificate.config.security.OAuth2SecuredWebConfiguration;
import ch.admin.bag.covidcertificate.service.NotificationService;
import ch.admin.bag.covidcertificate.web.security.AuthorizationInterceptor;
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
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = {NotificationsController.class})
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(roles = {"bag-cc-superuser"})
@DisplayName("Tests for NotificationsController")
class NotificationsControllerTest {
    @MockBean
    private AuthorizationInterceptor authorizationInterceptor;
    @MockBean
    private NotificationService notificationService;
    @MockBean
    private OAuth2SecuredWebConfiguration.OAuth2SecuredWebMvcConfiguration oAuth2SecuredWebMvcConfiguration;
    @Autowired
    private MockMvc mockMvc;

    private static final String URL = "/api/v1/notifications";
    private static final JFixture fixture = new JFixture();
    private final ObjectMapper mapper = new ObjectMapper();
    private EditNotificationDto validEditNotificationDto;
    private CreateNotificationDto validCreateNotificationDto;

    @BeforeEach
    void beforeEach() {
        when(authorizationInterceptor.preHandle(any(), any(), any())).thenReturn(true);
        this.validEditNotificationDto = EditNotificationDto.builder()
                .id(UUID.randomUUID())
                .type(NotificationType.INFO)
                .content(new NotificationContentDto("de", "fr", "it", "en"))
                .isClosable(true)
                .startTime(LocalDateTime.now().minusHours(1))
                .endTime(LocalDateTime.now().plusHours(1)).build();
        this.validCreateNotificationDto = CreateNotificationDto.builder()
                .type(NotificationType.INFO)
                .content(new NotificationContentDto("de", "fr", "it", "en"))
                .isClosable(true)
                .startTime(LocalDateTime.now().minusHours(1))
                .endTime(LocalDateTime.now().plusHours(1)).build();
    }

    @Nested
    @DisplayName("Tests for get")
    class GetAllNotificationsTest {
        @Test
        @DisplayName("Given no notifications are present, when called, it should return 204 No Content")
        void GetTest2() throws Exception {
            // given
            List<EditNotificationDto> notifications = Collections.emptyList();

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
        @DisplayName("Given notifications are present, when called, it should return 200 OK")
        void GetTest3() throws Exception {
            // given
            var notifications = List.of(new EditNotificationDto());

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
            var notifications = List.of(new EditNotificationDto());

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
    class CreateNotificationsTest {

        @BeforeEach
        void beforeEach() {
            lenient().doNothing().when(notificationService).createNotification(any());
        }

        @Test
        @DisplayName("Given notifications are valid, when called, it should return status 201 Created")
        void PostTest2() throws Exception {
            // given
            var notificationsStr = mapper.writeValueAsString(validCreateNotificationDto);

            // when then
            mockMvc.perform(post(URL)
                            .accept(MediaType.ALL_VALUE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", fixture.create(String.class))
                            .content(notificationsStr))
                    .andExpect(status().isCreated());

            verify(notificationService).createNotification(any());
        }

        @ParameterizedTest
        @CsvFileSource(resources = "/csv/create_notifications_dto.csv", delimiter = 'þ')
        @DisplayName("Given notification is invalid, when called, it should return status 400 Bad Request")
        void PostTest3(String notification, String expectedErrMsg) throws Exception {
            // given
            var request = post(URL)
                    .accept(MediaType.ALL_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", fixture.create(String.class));

            if (Objects.nonNull(notification)) {
                request.content(notification);
            }

            // when then
            mockMvc.perform(request)
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(Matchers.containsString(expectedErrMsg)));

        }

        @Test
        @DisplayName("Given 'end' is in past, when called, it should return status 400 Bad Request")
        void PostTest4() throws Exception {
            validCreateNotificationDto.setEndTime(LocalDateTime.now().minusHours(1));
            validCreateNotificationDto.setStartTime(LocalDateTime.now().minusHours(2));
            var notificationsStr = mapper.writeValueAsString(validCreateNotificationDto);

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
    @DisplayName("Tests for put")
    class EditNotificationsTest {

        @BeforeEach
        void beforeEach() {
            lenient().doNothing().when(notificationService).editNotification(any());
        }

        @Test
        @DisplayName("Given notifications are valid, when called, it should return status 201 Created")
        void PostTest2() throws Exception {
            // given
            var notificationsStr = mapper.writeValueAsString(validEditNotificationDto);

            // when then
            mockMvc.perform(put(URL)
                            .accept(MediaType.ALL_VALUE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", fixture.create(String.class))
                            .content(notificationsStr))
                    .andExpect(status().isOk());

            verify(notificationService).editNotification(any());
        }

        @ParameterizedTest
        @CsvFileSource(resources = "/csv/edit_notifications_dto.csv", delimiter = 'þ')
        @DisplayName("Given notification is invalid, when called, it should return status 400 Bad Request")
        void PostTest3(String notification, String expectedErrMsg) throws Exception {
            // given
            var request = put(URL)
                    .accept(MediaType.ALL_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", fixture.create(String.class));

            if (Objects.nonNull(notification)) {
                request.content(notification);
            }

            // when then
            mockMvc.perform(request)
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(Matchers.containsString(expectedErrMsg)));

        }

        @Test
        @DisplayName("Given 'end' is in past, when called, it should return status 200 OK")
        void PostTest4() throws Exception {
            validEditNotificationDto.setEndTime(LocalDateTime.now(ZoneOffset.UTC).minusHours(1));
            validEditNotificationDto.setStartTime(LocalDateTime.now(ZoneOffset.UTC).minusHours(2));
            var notificationsStr = mapper.writeValueAsString(validEditNotificationDto);

            // given
            var request = put(URL)
                    .accept(MediaType.ALL_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(notificationsStr)
                    .header("Authorization", fixture.create(String.class));

            // when then
            mockMvc.perform(request)
                    .andExpect(status().isOk());

        }

    }

    @Nested
    @DisplayName("Tests for delete")
    class RemoveNotificationsTest {

        @BeforeEach
        void beforeEach() {
            lenient().doNothing().when(notificationService).removeNotifications(any());
        }

        @Test
        @DisplayName("Given notifications are valid, when called, it should return 201 Created")
        void DeleteTest2() throws Exception {
            // when then
            mockMvc.perform(delete(URL + "/123")
                            .accept(MediaType.ALL_VALUE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", fixture.create(String.class)))
                    .andExpect(status().isOk());

            verify(notificationService).removeNotifications(any());
        }
    }
}