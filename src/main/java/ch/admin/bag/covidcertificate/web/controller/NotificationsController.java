package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.request.NotificationDto;
import ch.admin.bag.covidcertificate.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Slf4j
@Validated
public class NotificationsController {

    private final NotificationService notificationService;

    @GetMapping("")
    public ResponseEntity<List<NotificationDto>> getAllNotifications() {
        log.info("Call to get all notifications.");
        var notifications = notificationService.readNotifications();
        var responseStatus = notifications.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK;
        return new ResponseEntity<>(notifications, responseStatus);
    }

    @PostMapping("")
    public ResponseEntity<Void> writeNotifications(@RequestBody @NotEmpty List<@Valid NotificationDto> notifications) {
        log.info("Call of write notifications.");
        notificationService.writeNotifications(notifications);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("")
    public ResponseEntity<Void> removeNotifications() {
        log.info("Call of deleting notifications.");
        notificationService.removeNotifications();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
