package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.Constants;
import ch.admin.bag.covidcertificate.api.exception.NotificationException;
import ch.admin.bag.covidcertificate.api.request.NotificationDto;
import ch.admin.bag.covidcertificate.domain.Notification;
import ch.admin.bag.covidcertificate.domain.NotificationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private static final String NOTIFICATIONS_CACHE_NAME = "notifications";

    private final NotificationRepository notificationRepository;

    private final ObjectMapper mapper;

    @Cacheable(NOTIFICATIONS_CACHE_NAME)
    public List<NotificationDto> readNotifications() {
        log.info("Read all notifications");
        mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);

        var notification = this.notificationRepository.findAll().stream().findFirst();
        if (notification.isEmpty()) {
            return Collections.emptyList();
        }

        try {
            NotificationDto[] notifications = mapper.readValue(notification.get().getContent(), NotificationDto[].class);
            return Arrays.asList(notifications);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new NotificationException(Constants.NOTIFICATION_MAPPING_ERROR);
        }
    }

    @CacheEvict(value = NOTIFICATIONS_CACHE_NAME, allEntries = true)
    public void writeNotifications(List<NotificationDto> notifications) {
        log.info("Write notifications");

        if (this.notificationRepository.count() > 0) {
            throw new NotificationException(Constants.NOTIFICATION_ALREADY_EXISTING_ERROR);
        }

        try {
            String notificationsJson = mapper.writer().writeValueAsString(notifications);
            this.notificationRepository.save(new Notification(notificationsJson));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new NotificationException(Constants.NOTIFICATION_MAPPING_ERROR);
        }
    }

    @CacheEvict(value = NOTIFICATIONS_CACHE_NAME, allEntries = true)
    public void removeNotifications() {
        log.info("Remove notifications");
        this.notificationRepository.deleteAll();
    }
}
