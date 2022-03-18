package ch.admin.bag.covidcertificate.api.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
@Slf4j
public class ResponseStatusExceptionHandler {

    @ExceptionHandler(value = {NotificationException.class})
    protected ResponseEntity<Object> notificationException(NotificationException ex) {
        return new ResponseEntity<>(ex.getError(), ex.getError().getHttpStatus());
    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    protected ResponseEntity<Object> validationException(ConstraintViolationException e) {
        var violation = e.getConstraintViolations().stream().findFirst();
        var message = "Validation failed.";
        if (violation.isPresent()) {
            message = "Validation failed: " + violation.get().getMessage();
        }
        return new ResponseEntity<>(new NotificationError(460, message, HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {AccessDeniedException.class, SecurityException.class})
    protected ResponseEntity<Object> handleAccessDeniedException() {
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    protected ResponseEntity<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return new ResponseEntity<>(new NotificationError(462, "Malformed Request.", HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {Exception.class})
    protected ResponseEntity<Object> handleException(Exception e) {
        log.error("Exception", e);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
