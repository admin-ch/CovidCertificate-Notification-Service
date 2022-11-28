package ch.admin.bag.covidcertificate.api.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

public class NotInPastValidator implements
        ConstraintValidator<NotInPast, LocalDateTime> {

    @Override
    public boolean isValid(LocalDateTime date,
                           ConstraintValidatorContext cxt) {
        // We add a threshold of 15 minutes to be valid in the past since the creation of the notification
        // can take some time by the user.
        return Objects.isNull(date) || date.isAfter(LocalDateTime.now(ZoneOffset.UTC).minusMinutes(15));
    }
}