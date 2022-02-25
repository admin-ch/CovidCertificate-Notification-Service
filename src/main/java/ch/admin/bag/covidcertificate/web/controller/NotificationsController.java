package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationsController {
    private final SecurityHelper securityHelper;
    private final NotificationService notificationService;

    @GetMapping("")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public ResponseEntity<String> getAllNotifications(HttpServletRequest request) {
        log.info("Call to get all notifications.");
        securityHelper.authorizeUser(request);
        var notifications = notificationService.readNotifications();
        if (notifications.isEmpty()) {
            return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(notifications.get().getMessages(), HttpStatus.OK);
        }
    }

    @PostMapping("")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public ResponseEntity writeNotifications(@RequestBody String notifications, HttpServletRequest request) {
        log.info("Call of write notifications.");
        securityHelper.authorizeUser(request);
        notificationService.writeNotifications(notifications);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public ResponseEntity removeNotifications(HttpServletRequest request) {
        log.info("Call of deleting notifications.");
        securityHelper.authorizeUser(request);
        notificationService.removeNotifications();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }
}
