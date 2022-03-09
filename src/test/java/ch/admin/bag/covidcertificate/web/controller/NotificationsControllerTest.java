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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = {NotificationsController.class})
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(roles = {"bag-cc-superuser"})
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
    class GetAllNotificationsTest {
        @Test
        void whenCalled_thenCallAuthorize() throws Exception {
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
        void whenNoNotificationsPresent_thenReturnStatusNoContent() throws Exception {
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
        void whenNoNotificationsPresent_thenReturnStatusOk() throws Exception {
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
        void whenNoNotificationsPresent_thenReturnNotifications() throws Exception {
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
    class WriteNotificationsTest {

        @BeforeEach
        void beforeEach() {
            lenient().doNothing().when(notificationService).writeNotifications(any());
        }

        @Test
        void whenCalled_thenCallAuthorize() throws Exception {
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
        void whenNoNotificationsValid_thenReturnStatusCreated() throws Exception {
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
        void whenInvalidData_thenReturnBadRequest(String notifications, String expectedErrMsg) throws Exception {
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
        void whenEndIsInPast_thenReturnBadRequest() throws Exception {
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
    class RemoveNotificationsTest {

        @BeforeEach
        void beforeEach() {
            lenient().doNothing().when(notificationService).removeNotifications();
        }

        @Test
        void whenCalled_thenCallAuthorize() throws Exception {
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
        void whenNoNotificationsValid_thenReturnStatusCreated() throws Exception {
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