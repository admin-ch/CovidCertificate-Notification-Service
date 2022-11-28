package ch.admin.bag.covidcertificate.api.validation;

import ch.admin.bag.covidcertificate.api.request.CreateNotificationDto;
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
    void validateTest1() {
        // given
        var notificationDto = CreateNotificationDto.builder().build();
        notificationDto.setEndTime(now);

        // when
        var result = validator.isValid(notificationDto, null);

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("Given 'end' is 'null', when validated, it should return 'true'")
    void validateTest2() {
        // given
        var notificationDto = CreateNotificationDto.builder().build();
        notificationDto.setStartTime(now);

        // when
        var result = validator.isValid(notificationDto, null);

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("Given 'start' is before 'end', when validated, it should return 'true'")
    void validateTest3() {
        // given
        var notificationDto = CreateNotificationDto.builder()
                .startTime(now)
                .endTime(now.plusSeconds(1))
                .isClosable(true)
                .build();

        // when
        var result = validator.isValid(notificationDto, null);

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("Given 'start' is same as 'end', when validated, it should return 'true'")
    void validateTest4() {
        // given
        var notificationDto = CreateNotificationDto.builder()
                .startTime(now)
                .endTime(now)
                .isClosable(true)
                .build();
        // when
        var result = validator.isValid(notificationDto, null);

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("Given 'end' is before 'start', when validated, it should return 'false'")
    void validateTest5() {
        // given
        var notificationDto = CreateNotificationDto.builder()
                .startTime(now)
                .endTime(now.minusSeconds(1))
                .isClosable(true)
                .build();
        // when
        var result = validator.isValid(notificationDto, null);

        // then
        assertFalse(result);
    }
}