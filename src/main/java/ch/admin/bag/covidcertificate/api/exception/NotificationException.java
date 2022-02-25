package ch.admin.bag.covidcertificate.api.exception;

import lombok.Getter;
import org.springframework.core.NestedRuntimeException;

@Getter
public class NotificationException extends NestedRuntimeException {
    private final NotificationError error;

    public NotificationException(NotificationError error) {
        super(error.getErrorMessage());
        this.error = error;
    }
}
