package ch.admin.bag.covidcertificate.api.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NotInPastValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotInPast {
    String message() default "Date must not be in the past";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
