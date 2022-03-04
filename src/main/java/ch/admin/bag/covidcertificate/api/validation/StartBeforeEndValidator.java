package ch.admin.bag.covidcertificate.api.validation;

import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class StartBeforeEndValidator
        implements ConstraintValidator<StartBeforeEnd, Object> {

    private String startField;
    private String endField;

    public void initialize(StartBeforeEnd constraintAnnotation) {
        this.startField = constraintAnnotation.startField();
        this.endField = constraintAnnotation.endField();
    }

    public boolean isValid(Object value,
                           ConstraintValidatorContext context) {

        LocalDateTime start = (LocalDateTime) new BeanWrapperImpl(value)
                .getPropertyValue(startField);
        LocalDateTime end = (LocalDateTime) new BeanWrapperImpl(value)
                .getPropertyValue(endField);


        if (start == null && end == null) return true;
        if ((start != null && end == null) || (start == null && end != null)) return false;
        return start.isBefore(end);
    }
}