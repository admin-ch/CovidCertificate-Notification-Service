package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.mapper.NotificationMapper;
import ch.admin.bag.covidcertificate.api.request.CreateNotificationDto;
import ch.admin.bag.covidcertificate.api.request.EditNotificationDto;
import ch.admin.bag.covidcertificate.domain.Notification;
import ch.admin.bag.covidcertificate.domain.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    public List<EditNotificationDto> readNotifications() {
        log.info("Read all notifications");

        var notifications = this.notificationRepository.findAll();
        if (notifications.isEmpty()) {
            return Collections.emptyList();
        }
        log.info(notifications.size() + " notifications present");

        return notificationMapper.fromEntity(notifications);
    }

    public void createNotification(CreateNotificationDto notification) {
        log.info("Create notification");
        this.notificationRepository.save(notificationMapper.fromDto(notification));
    }

    public void editNotification(EditNotificationDto notification) {
        log.info("Write notification");
        this.notificationRepository.save(notificationMapper.fromDto(notification));
    }

    public void removeNotifications(String id) {
        log.info("Remove notification with id: " + id);
        this.notificationRepository.delete(Notification.builder().id(UUID.fromString(id)).build());
    }
}
