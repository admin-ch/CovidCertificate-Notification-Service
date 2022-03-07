package ch.admin.bag.covidcertificate.api.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.util.Objects;

public class NotInPastValidator implements
        ConstraintValidator<NotInPast, LocalDateTime> {

    @Override
    public void initialize(NotInPast date) {
    }

    @Override
    public boolean isValid(LocalDateTime date,
                           ConstraintValidatorContext cxt) {
        return Objects.isNull(date) || date.isAfter(LocalDateTime.now());
    }
}