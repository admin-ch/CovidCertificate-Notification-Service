package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.exception.NotificationValidationException;
import ch.admin.bag.covidcertificate.api.request.NotificationDto;
import ch.admin.bag.covidcertificate.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import java.util.List;

import static ch.admin.bag.covidcertificate.api.error.RestError.restValidationError;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationsController {

    private final Validator validator;
    private final SecurityHelper securityHelper;
    private final NotificationService notificationService;

    @GetMapping("")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public ResponseEntity<List<NotificationDto>> getAllNotifications(HttpServletRequest request) {
        log.info("Call to get all notifications.");
        securityHelper.authorizeUser(request);
        var notifications = notificationService.readNotifications();
        var responseStatus = notifications.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK;
        return new ResponseEntity(notifications, responseStatus);
    }

    @PostMapping("")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public ResponseEntity writeNotifications(@RequestBody @NotNull @Valid List<NotificationDto> notifications, HttpServletRequest request) {
        log.info("Call of write notifications.");
        securityHelper.authorizeUser(request);

        for (NotificationDto notification : notifications) {
            // List contents are not being validated with `@Valid`, therefor validating manually.
            var violations = this.validator.validate(notification);
            if (!violations.isEmpty()) {
                var violation = violations.stream().findFirst().orElseThrow();
                throw new NotificationValidationException(restValidationError(violation.getPropertyPath().toString() + " " + violation.getMessage()));
            }
            notification.validate();
        }
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
