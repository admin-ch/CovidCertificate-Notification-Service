package ch.admin.bag.covidcertificate.api.validation;

import org.hibernate.validator.internal.util.annotation.AnnotationDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Tests for NotInPastValidator")
class NotInPastValidatorTest {
    private final NotInPastValidator validator = new NotInPastValidator();

    @BeforeEach
    public void beforeEach() {
        final var constraint = new AnnotationDescriptor.Builder<>(NotInPast.class).build().getAnnotation();
        validator.initialize(constraint);
    }

    @Test
    @DisplayName("Given date is 'null', when validated, it should return 'true'")
    void validateTest1() {
        // given
        LocalDateTime date = null;

        // when
        var result = validator.isValid(date, null);

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("Given date is barely in the future, when validated, it should return 'true'")
    void validateTest2() {
        // given
        LocalDateTime date = LocalDateTime.now().plusSeconds(1);

        // when
        var result = validator.isValid(date, null);

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("Given date is now, when validated, it should return 'true'")
    void validateTest3() {

        // given
        LocalDateTime currentLocalDate = LocalDateTime.of(2022, 2, 2, 0, 0, 0, 0);
        try (MockedStatic<LocalDateTime> topDateTimeUtilMock = Mockito.mockStatic(LocalDateTime.class)) {
            topDateTimeUtilMock.when(() -> LocalDateTime.now(ZoneOffset.UTC)).thenReturn(currentLocalDate);

            // when
            var result = validator.isValid(currentLocalDate, null);

            // then
            assertTrue(result);
        }
    }

    @Test
    @DisplayName("Given date is long in the future, when validated, it should return 'true'")
    void validateTest4() {
        // given
        LocalDateTime date = LocalDateTime.now(ZoneOffset.UTC).plusYears(1);

        // when
        var result = validator.isValid(date, null);

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("Given date is barely in past, when validated, it should return 'true'")
    void validateTest5() {
        // given
        LocalDateTime date = LocalDateTime.now(ZoneOffset.UTC).minusSeconds(1);

        // when
        var result = validator.isValid(date, null);

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("Given date is more than 15 minutes in past, when validated, it should return 'false'")
    void validateTest6() {
        // given
        LocalDateTime date = LocalDateTime.now(ZoneOffset.UTC).minusMinutes(16);

        // when
        var result = validator.isValid(date, null);

        // then
        assertFalse(result);
    }

    @Test
    @DisplayName("Given date is long in past, when validated, it should return 'false'")
    void validateTest7() {
        // given
        LocalDateTime date = LocalDateTime.now(ZoneOffset.UTC).minusYears(1);

        // when
        var result = validator.isValid(date, null);

        // then
        assertFalse(result);
    }
}