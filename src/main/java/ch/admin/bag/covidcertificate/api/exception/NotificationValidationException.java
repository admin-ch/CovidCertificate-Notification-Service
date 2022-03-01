package ch.admin.bag.covidcertificate.api.exception;

import ch.admin.bag.covidcertificate.api.error.RestError;
import lombok.Getter;
import org.springframework.core.NestedRuntimeException;

@Getter
public class NotificationValidationException extends NestedRuntimeException {
    private final RestError error;

    public NotificationValidationException(RestError error) {
        super(error.getErrorMessage());
        this.error = error;
    }
}
