package ch.admin.bag.covidcertificate.api.validation;

import ch.admin.bag.covidcertificate.api.request.NotificationDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StartBeforeEndValidator
        implements ConstraintValidator<StartBeforeEnd, NotificationDto> {

    public void initialize(StartBeforeEnd constraintAnnotation) {
    }

    public boolean isValid(NotificationDto value,
                           ConstraintValidatorContext context) {

        var start = value.getStart();
        var end = value.getEnd();

        if (start == null || end == null) return true;
        return start.isBefore(end);
    }
}