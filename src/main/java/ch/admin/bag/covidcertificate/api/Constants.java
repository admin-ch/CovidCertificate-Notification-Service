package ch.admin.bag.covidcertificate.api;

import ch.admin.bag.covidcertificate.api.exception.NotificationError;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {
    // Utils
    public static final String PREFERRED_USERNAME_CLAIM_KEY = "preferred_username";
    public static final NotificationError NOTIFICATION_MAPPING_ERROR = new NotificationError(550, "Notification could not be mapped to DTO.", HttpStatus.INTERNAL_SERVER_ERROR);
}
