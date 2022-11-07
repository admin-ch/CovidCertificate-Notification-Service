package ch.admin.bag.covidcertificate.api.validation;

import ch.admin.bag.covidcertificate.api.request.HasStartEnd;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StartBeforeEndValidator
        implements ConstraintValidator<StartBeforeEnd, HasStartEnd> {

    @Override
    public boolean isValid(HasStartEnd value,
                           ConstraintValidatorContext context) {

        var start = value.getStartTime();
        var end = value.getEndTime();

        if (start == null || end == null) return true;
        return start.isBefore(end) || start.isEqual(end);
    }
}