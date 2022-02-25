package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.domain.Notification;
import ch.admin.bag.covidcertificate.domain.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private static final String NOTIFICATIONS_CACHE_NAME = "notifications";

    private final NotificationRepository notificationRepository;

    @Cacheable(NOTIFICATIONS_CACHE_NAME)
    public Optional<Notification> readNotifications() {
        log.info("Read all notifications");
        return this.notificationRepository.findAll().stream().findFirst();
    }

    @CacheEvict(value = NOTIFICATIONS_CACHE_NAME, allEntries = true)
    public void writeNotifications(String notifications) {
        log.info("Write notifications");
        this.notificationRepository.deleteAll();
        this.notificationRepository.save(new Notification(notifications));
    }

    @CacheEvict(value = NOTIFICATIONS_CACHE_NAME, allEntries = true)
    public void removeNotifications() {
        log.info("Remove notifications");
        this.notificationRepository.deleteAll();
    }
}
