package ch.admin.bag.covidcertificate.api.validation;

import ch.admin.bag.covidcertificate.api.request.NotificationDto;
import org.hibernate.validator.internal.util.annotation.AnnotationDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StartBeforeEndValidatorTest {
    private final StartBeforeEndValidator validator = new StartBeforeEndValidator();
    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    public void beforeEach() {
        final var constraint = new AnnotationDescriptor.Builder<>(StartBeforeEnd.class).build().getAnnotation();
        validator.initialize(constraint);
    }

    @Test
    public void ifStartIsNull_thenReturnTrue() {
        // given
        var notificationDto = new NotificationDto();
        notificationDto.setEnd(now);

        // when
        var result = validator.isValid(notificationDto, null);

        // then
        assertTrue(result);
    }

    @Test
    public void ifEndIsNull_thenReturnTrue() {
        // given
        var notificationDto = new NotificationDto();
        notificationDto.setStart(now);

        // when
        var result = validator.isValid(notificationDto, null);

        // then
        assertTrue(result);
    }

    @Test
    public void ifStartIsBeforeEnd_thenReturnTrue() {
        // given
        var notificationDto = new NotificationDto(null, null, now, now.plusSeconds(1));

        // when
        var result = validator.isValid(notificationDto, null);

        // then
        assertTrue(result);
    }

    @Test
    public void ifStartIsSameAsEnd_thenReturnFalse() {
        // given
        var notificationDto = new NotificationDto(null, null, now, now);

        // when
        var result = validator.isValid(notificationDto, null);

        // then
        assertFalse(result);
    }

    @Test
    public void ifEndIsBeforeEnd_thenReturnFalse() {
        // given
        var notificationDto = new NotificationDto(null, null, now, now.minusSeconds(1));

        // when
        var result = validator.isValid(notificationDto, null);

        // then
        assertFalse(result);
    }
}