package ch.admin.bag.covidcertificate.api.exception;

import ch.admin.bag.covidcertificate.api.error.RestError;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ResponseStatusExceptionHandler {

    @ExceptionHandler(value = {NotificationValidationException.class})
    protected ResponseEntity<RestError> createCertificateConflict(NotificationValidationException ex) {
        return handleError(ex.getError());
    }

    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    protected ResponseEntity<RestError> notReadableRequestPayload(HttpMessageNotReadableException ex) {
        RestError error;
        try {
            var rootException = (InvalidFormatException) ex.getCause();
            assert rootException != null;
            var errorMessage = "Unable to parse " + rootException.getValue() + " to " + rootException.getTargetType();
            log.warn("HttpMessage with invalid format received: ", rootException);
            error = new RestError(HttpStatus.BAD_REQUEST.value(), errorMessage, HttpStatus.BAD_REQUEST);
        } catch (ClassCastException | AssertionError processingException) {
            log.warn("HttpMessage is not readable: ", ex);
            error = new RestError(HttpStatus.BAD_REQUEST.value(), "Http message not readable", HttpStatus.BAD_REQUEST);
        }
        return handleError(error);
    }

    @ExceptionHandler(value = {Exception.class})
    protected ResponseEntity<Object> handleException(Exception e) {
        log.error("Exception during processing", e);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<RestError> handleError(RestError RestError) {
        log.warn("Error {}", RestError);
        return new ResponseEntity<>(RestError, RestError.getHttpStatus());
    }
}
