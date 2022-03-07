package ch.admin.bag.covidcertificate.api.validation;

import org.hibernate.validator.internal.util.annotation.AnnotationDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotInPastValidatorTest {
    private final NotInPastValidator validator = new NotInPastValidator();
    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    public void beforeEach() {
        final var constraint = new AnnotationDescriptor.Builder<>(NotInPast.class).build().getAnnotation();
        validator.initialize(constraint);
    }

    @Test
    public void ifDateIsNull_thenReturnTrue() {
        // given
        LocalDateTime date = null;

        // when
        var result = validator.isValid(date, null);

        // then
        assertTrue(result);
    }

    @Test
    public void ifDateIsBarelyInTheFuture_thenReturnTrue() {
        // given
        LocalDateTime date = LocalDateTime.now().plusSeconds(1);

        // when
        var result = validator.isValid(date, null);

        // then
        assertTrue(result);
    }

    @Test
    public void ifDateIsLongInTheFuture_thenReturnTrue() {
        // given
        LocalDateTime date = LocalDateTime.now().plusYears(1);

        // when
        var result = validator.isValid(date, null);

        // then
        assertTrue(result);
    }

    @Test
    public void ifDateIsBarelyInPast_thenReturnFalse() {
        // given
        LocalDateTime date = LocalDateTime.now().minusSeconds(1);

        // when
        var result = validator.isValid(date, null);

        // then
        assertFalse(result);
    }

    @Test
    public void ifDateIsLongInPast_thenReturnFalse() {
        // given
        LocalDateTime date = LocalDateTime.now().minusYears(1);

        // when
        var result = validator.isValid(date, null);

        // then
        assertFalse(result);
    }
}