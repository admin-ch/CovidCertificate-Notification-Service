package ch.admin.bag.covidcertificate.api;

import ch.admin.bag.covidcertificate.api.exception.AuthorizationError;
import ch.admin.bag.covidcertificate.api.exception.NotificationError;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {
    // Utils
    public static final String PREFERRED_USERNAME_CLAIM_KEY = "preferred_username";

    // Errors
    public static final NotificationError NOTIFICATION_MAPPING_ERROR = new NotificationError(550, "Notification could not be mapped to DTO.", HttpStatus.INTERNAL_SERVER_ERROR);
    public static final AuthorizationError NO_FUNCTION_CONFIGURED = new AuthorizationError(480, "Function with uri %s is not configured", HttpStatus.INTERNAL_SERVER_ERROR);
    public static final AuthorizationError FORBIDDEN = new AuthorizationError(481, "No sufficient roles for feature with uri %s", HttpStatus.FORBIDDEN);
    public static final AuthorizationError ACCESS_DENIED_FOR_HIN_WITH_CH_LOGIN = new AuthorizationError(482, "Access denied for HIN with CH-Login", HttpStatus.FORBIDDEN);


    // Error messages
    public static final String ID_MUST_NOT_BE_NULL = "ID must not be null.";
    public static final String TYPE_MUST_NOT_BE_NULL = "Type must not be null.";
    public static final String CONTENT_MUST_NOT_BE_NULL = "Content must not be null.";
    public static final String IS_CLOSABLE_MUST_NOT_BE_NULL = "isClosable must not be null.";
    public static final String START_MUST_NOT_BE_NULL = "Start must not be null.";
    public static final String END_MUST_NOT_BE_NULL = "End must not be null.";
    public static final String END_MUST_NOT_BE_IN_PAST = "End must not be in the past.";
    public static final String START_HAS_TO_BE_BEFORE_END = "Start has to be before end.";
    public static final String DE_MUST_NOT_BE_EMPTY = "de of content must not be empty.";
    public static final String FR_MUST_NOT_BE_EMPTY = "fr of content must not be empty.";
    public static final String IT_MUST_NOT_BE_EMPTY = "it of content must not be empty.";
    public static final String EN_MUST_NOT_BE_EMPTY = "en of content must not be empty.";
}
