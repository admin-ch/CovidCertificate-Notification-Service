package ch.admin.bag.covidcertificate.api.validation;

import ch.admin.bag.covidcertificate.api.request.NotificationDto;
import org.hibernate.validator.internal.util.annotation.AnnotationDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Tests for StartBeforeEndValidator")
class StartBeforeEndValidatorTest {
    private final StartBeforeEndValidator validator = new StartBeforeEndValidator();
    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    public void beforeEach() {
        final var constraint = new AnnotationDescriptor.Builder<>(StartBeforeEnd.class).build().getAnnotation();
        validator.initialize(constraint);
    }

    @Test
    @DisplayName("Given 'start' is 'null', when validated, it should return 'true'")
    public void validateTest1() {
        // given
        var notificationDto = new NotificationDto();
        notificationDto.setEnd(now);

        // when
        var result = validator.isValid(notificationDto, null);

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("Given 'end' is 'null', when validated, it should return 'true'")
    public void validateTest2() {
        // given
        var notificationDto = new NotificationDto();
        notificationDto.setStart(now);

        // when
        var result = validator.isValid(notificationDto, null);

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("Given 'start' is before 'end', when validated, it should return 'true'")
    public void validateTest3() {
        // given
        var notificationDto = new NotificationDto(null, null, now, now.plusSeconds(1));

        // when
        var result = validator.isValid(notificationDto, null);

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("Given 'start' is same as 'end', when validated, it should return 'false'")
    public void validateTest4() {
        // given
        var notificationDto = new NotificationDto(null, null, now, now);

        // when
        var result = validator.isValid(notificationDto, null);

        // then
        assertFalse(result);
    }

    @Test
    @DisplayName("Given 'end' is before 'start', when validated, it should return 'false'")
    public void validateTest5() {
        // given
        var notificationDto = new NotificationDto(null, null, now, now.minusSeconds(1));

        // when
        var result = validator.isValid(notificationDto, null);

        // then
        assertFalse(result);
    }
}