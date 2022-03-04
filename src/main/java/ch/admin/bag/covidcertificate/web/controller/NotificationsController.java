package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.request.NotificationDto;
import ch.admin.bag.covidcertificate.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    private final SecurityHelper securityHelper;
    private final NotificationService notificationService;

    @GetMapping("")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public ResponseEntity<List<NotificationDto>> getAllNotifications(HttpServletRequest request) {
        log.info("Call to get all notifications.");
        securityHelper.authorizeUser(request);
        var notifications = notificationService.readNotifications();
        var responseStatus = notifications.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK;
        return new ResponseEntity<>(notifications, responseStatus);
    }

    @PostMapping("")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public ResponseEntity<Void> writeNotifications(@RequestBody @NotEmpty List<@Valid NotificationDto> notifications, HttpServletRequest request) {
        log.info("Call of write notifications.");
        securityHelper.authorizeUser(request);
        notificationService.writeNotifications(notifications);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public ResponseEntity<Void> removeNotifications(HttpServletRequest request) {
        log.info("Call of deleting notifications.");
        securityHelper.authorizeUser(request);
        notificationService.removeNotifications();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }
}
