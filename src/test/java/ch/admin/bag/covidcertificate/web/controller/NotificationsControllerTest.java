package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.exception.ResponseStatusExceptionHandler;
import ch.admin.bag.covidcertificate.api.request.MessageDto;
import ch.admin.bag.covidcertificate.api.request.MessageType;
import ch.admin.bag.covidcertificate.api.request.NotificationDto;
import ch.admin.bag.covidcertificate.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class NotificationsControllerTest {
    @InjectMocks
    private NotificationsController controller;
    @Mock
    private SecurityHelper securityHelper;
    @Mock
    private NotificationService notificationService;

    private static final String URL = "/api/v1/notifications";
    private MockMvc mockMvc;
    private static final JFixture fixture = new JFixture();
    private final ObjectMapper mapper = new ObjectMapper();
    private NotificationDto validNotificationDto;

    @BeforeEach
    void setupMocks() {
        this.mockMvc = standaloneSetup(controller, new ResponseStatusExceptionHandler()).build();
        lenient().when(securityHelper.authorizeUser(any())).thenReturn(true);
        this.validNotificationDto = new NotificationDto(
                MessageType.INFO,
                new MessageDto("de", "fr", "it", "en"),
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now()
        );
    }

    @Nested
    class GetAllNotificationsTest {
        @Test
        void whenNoNotificationsPresent_thenReturnStatusNoContent() throws Exception {
            // given
            List<NotificationDto> notifications = Collections.emptyList();

            // when
            when(securityHelper.authorizeUser(any())).thenReturn(true);
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
            when(securityHelper.authorizeUser(any())).thenReturn(true);
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
            when(securityHelper.authorizeUser(any())).thenReturn(true);
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

            verify(securityHelper).authorizeUser(any());
            verify(notificationService).writeNotifications(any());
        }
    }

    @Nested
    class RemoveNotificationsTest {

        @BeforeEach
        void beforeEach() {
            lenient().doNothing().when(notificationService).removeNotifications();
        }

        @Test
        void whenNoNotificationsValid_thenReturnStatusCreated() throws Exception {
            // when then
            mockMvc.perform(delete(URL)
                            .accept(MediaType.ALL_VALUE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", fixture.create(String.class)))
                    .andExpect(status().isNoContent());

            verify(securityHelper).authorizeUser(any());
            verify(notificationService).removeNotifications();
        }
    }
}