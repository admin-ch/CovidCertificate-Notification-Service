package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.request.NotificationDto;
import ch.admin.bag.covidcertificate.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    public ResponseEntity<List<NotificationDto>> getAllNotifications(HttpServletRequest request) {
        log.info("Call to get all notifications.");
        var notifications = notificationService.readNotifications();
        var responseStatus = notifications.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK;
        return new ResponseEntity<>(notifications, responseStatus);
    }

    @PostMapping("")
    public ResponseEntity<Void> writeNotifications(@RequestBody @NotEmpty List<@Valid NotificationDto> notifications, HttpServletRequest request) {
        log.info("Call of write notifications.");
        notificationService.writeNotifications(notifications);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("")
    public ResponseEntity<Void> removeNotifications(HttpServletRequest request) {
        log.info("Call of deleting notifications.");
        notificationService.removeNotifications();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }
}
