package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.exception.NotificationException;
import ch.admin.bag.covidcertificate.api.request.MessageDto;
import ch.admin.bag.covidcertificate.api.request.MessageType;
import ch.admin.bag.covidcertificate.api.request.NotificationDto;
import ch.admin.bag.covidcertificate.domain.Notification;
import ch.admin.bag.covidcertificate.domain.NotificationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static ch.admin.bag.covidcertificate.api.Constants.NOTIFICATION_ALREADY_EXISTING_ERROR;
import static ch.admin.bag.covidcertificate.api.Constants.NOTIFICATION_MAPPING_ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ObjectMapper mapper;

    private Notification notification;
    private NotificationDto notificationDto;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final String notificationJsonStr = "[\n" +
            "    {\n" +
            "        \"type\": \"INFO\",\n" +
            "        \"message\": {\n" +
            "            \"de\": \"de\",\n" +
            "            \"fr\": \"fr\",\n" +
            "            \"it\": \"it\",\n" +
            "            \"en\": \"en\"\n" +
            "        },\n" +
            "        \"start\": \"2022-03-09 11:30\",\n" +
            "        \"end\": \"2022-03-09 11:31\"\n" +
            "    }\n" +
            "]";

    @BeforeEach
    void beforeEach() {
        notification = new Notification(notificationJsonStr);
        notificationDto = new NotificationDto(MessageType.INFO, new MessageDto("de", "fr", "it", "en"), LocalDateTime.parse("2022-03-09 11:30", formatter), LocalDateTime.parse("2022-03-09 11:31", formatter));
    }

    @Nested
    public class ReadNotificationsTest {

        @BeforeEach
        void beforeEach() {
            when(notificationRepository.findAll()).thenReturn(List.of(notification));
        }


        @Test
        void whenNoNotificationsPresent_thenReturnEmptyList() {
            // given
            when(notificationRepository.findAll()).thenReturn(Collections.emptyList());

            // when
            var notifications = notificationService.readNotifications();

            // then
            assertEquals(notifications, Collections.emptyList());
        }

        @Test
        void whenNotificationsPresent_thenReturnListOfNotificationDto() throws JsonProcessingException {
            // given
            when(mapper.readValue(anyString(), ArgumentMatchers.<Class<NotificationDto[]>>any())).thenReturn(new NotificationDto[]{notificationDto});

            // when
            var notifications = notificationService.readNotifications();

            // then
            assertThat(notifications.get(0)).usingRecursiveComparison().isEqualTo(notificationDto);
        }

        @Test
        void whenMappingFails_thenThrowNotificationException() throws JsonProcessingException {
            // given
            when(mapper.readValue(anyString(), ArgumentMatchers.<Class<Notification[]>>any())).thenThrow(JsonProcessingException.class);

            // when then
            var e = assertThrows(NotificationException.class, notificationService::readNotifications);
            assertEquals(e.getError(), NOTIFICATION_MAPPING_ERROR);
        }
    }

    @Nested
    public class WriteNotificationsTest {

        @BeforeEach
        void beforeEach() {
            when(notificationRepository.findAll()).thenReturn(List.of());
        }

        @Test
        void whenNotificationsPresent_thenThrowNotificationException() {
            // given
            when(notificationRepository.findAll()).thenReturn(List.of(notification));

            // when then
            var e = assertThrows(NotificationException.class, () -> notificationService.writeNotifications(List.of(notificationDto)));
            assertEquals(e.getError(), NOTIFICATION_ALREADY_EXISTING_ERROR);
        }

        @Test
        void whenNoNotificationsPresent_thenSaveNotification() throws JsonProcessingException {
            // given
            when(notificationRepository.findAll()).thenReturn(Collections.emptyList());
            when(mapper.writer().writeValueAsString(any())).thenReturn(notificationJsonStr);

            // when
            notificationService.writeNotifications(List.of(notificationDto));

            // then
            verify(notificationRepository).save(argThat(argument -> argument.getContent().equals(notificationJsonStr)));
        }

        @Test
        void whenMappingFails_thenThrowNotificationException() throws JsonProcessingException {
            // given
            when(mapper.writer().writeValueAsString(any())).thenThrow(JsonProcessingException.class);

            // when then
            var e = assertThrows(NotificationException.class, () -> notificationService.writeNotifications(List.of(notificationDto)));
            assertEquals(e.getError(), NOTIFICATION_MAPPING_ERROR);
        }
    }

    @Nested
    public class RemoveNotificationsTest {

        @Test
        void whenCalled_thenNotificationsAreDeleted() {
            // given
            doNothing().when(notificationRepository).deleteAll();

            // when
            notificationService.removeNotifications();

            // then
            verify(notificationRepository).deleteAll();
        }
    }
}