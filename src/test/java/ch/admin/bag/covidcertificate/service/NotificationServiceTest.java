package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.mapper.NotificationMapper;
import ch.admin.bag.covidcertificate.api.request.EditNotificationDto;
import ch.admin.bag.covidcertificate.api.request.NotificationContentDto;
import ch.admin.bag.covidcertificate.api.request.NotificationType;
import ch.admin.bag.covidcertificate.domain.Notification;
import ch.admin.bag.covidcertificate.domain.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
@DisplayName("Tests for NotificationService")
class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationMapper notificationMapper;

    private Notification notification;
    private EditNotificationDto notificationDto;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final String notificationContentJsonStr = "{\n" +
            "            \"de\": \"de\",\n" +
            "            \"fr\": \"fr\",\n" +
            "            \"it\": \"it\",\n" +
            "            \"en\": \"en\"\n" +
            "        }";

    @BeforeEach
    void beforeEach() {
        notification = Notification.builder()
                .id(UUID.randomUUID())
                .type(NotificationType.INFO)
                .content(notificationContentJsonStr)
                .startTime(LocalDateTime.parse("2022-03-09 11:30", formatter))
                .endTime(LocalDateTime.parse("2022-03-09 11:31", formatter))
                .isClosable(true)
                .build();

        notificationDto = EditNotificationDto.builder()
                .id(UUID.randomUUID())
                .type(NotificationType.INFO)
                .content(new NotificationContentDto("de", "fr", "it", "en"))
                .isClosable(true)
                .startTime(LocalDateTime.parse("2022-03-09 11:30", formatter))
                .endTime(LocalDateTime.parse("2022-03-09 11:31", formatter))
                .build();
    }

    @Nested
    @DisplayName("Tests for readNotification")
    class ReadNotificationsTest {

        @BeforeEach
        void beforeEach() {
            when(notificationRepository.findAll()).thenReturn(List.of(notification));
        }


        @Test
        @DisplayName("Given no notifications are present, when called, it should return an empty collection")
        void readNotificationTest1() {
            // given
            when(notificationRepository.findAll()).thenReturn(Collections.emptyList());

            // when
            var notifications = notificationService.readNotifications();

            // then
            assertEquals(Collections.emptyList(), notifications);
        }

        @Test
        @DisplayName("Given notifications are present, when called, it should return the list of NotificationDto")
        void readNotificationTest2() {

            // given
            when(notificationMapper.fromEntity(anyList())).thenReturn(List.of(notificationDto));

            // when
            var notifications = notificationService.readNotifications();

            // then
            assertThat(notifications).usingRecursiveComparison().isEqualTo(List.of(notificationDto));
        }
    }

    @Nested
    @DisplayName("Tests for writeNotification")
    class WriteNotificationsTest {

        @Test
        @DisplayName("When called, it should save notifications")
        void writeNotificationTest1() {
            // when
            notificationService.editNotification(notificationDto);

            // then
            verify(notificationRepository, times(1)).save(any());
        }
    }

    @Nested
    @DisplayName("Tests for removeNotification")
    class RemoveNotificationsTest {

        @Test
        @DisplayName("When called, it should delete the notification")
        void removeNotificationTest1() {
            // given
            doNothing().when(notificationRepository).delete(any());

            // when
            notificationService.removeNotifications(UUID.randomUUID().toString());

            // then
            verify(notificationRepository, times(1)).delete(any());
        }
    }
}