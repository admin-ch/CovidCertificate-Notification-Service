package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.request.CreateNotificationDto;
import ch.admin.bag.covidcertificate.api.request.EditNotificationDto;
import ch.admin.bag.covidcertificate.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Slf4j
@Validated
public class NotificationsController {

    private final NotificationService notificationService;

    @GetMapping("")
    public ResponseEntity<List<EditNotificationDto>> getAllNotifications() {
        log.info("Call to get all notifications.");
        var notifications = notificationService.readNotifications();
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<Void> createNotifications(@RequestBody @Valid CreateNotificationDto notification) {
        log.info("Call of create notification.");
        notificationService.createNotification(notification);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("")
    public ResponseEntity<Void> editNotifications(@RequestBody @Valid EditNotificationDto notification) {
        log.info("Call of edit notification.");
        notificationService.editNotification(notification);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeNotifications(@PathVariable String id) {
        log.info("Call of deleting notifications.");
        notificationService.removeNotifications(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
