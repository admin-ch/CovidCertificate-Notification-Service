package ch.admin.bag.covidcertificate.api.validation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.internal.util.annotation.AnnotationDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StartBeforeEndValidatorTest {
    private final StartBeforeEndValidator validator = new StartBeforeEndValidator();
    private final LocalDateTime now = LocalDateTime.now();

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    static class TestHelper {
        private LocalDateTime start;
        private LocalDateTime end;
    }

    @BeforeEach
    public void beforeEach() {
        final var constraint = new AnnotationDescriptor.Builder<>(StartBeforeEnd.class, Map.ofEntries(Map.entry("startField", "start"), Map.entry("endField", "end"))).build().getAnnotation();
        validator.initialize(constraint);
    }

    @Test
    public void ifStartIsNull_thenReturnTrue() {
        // given
        var testHelper = new TestHelper();
        testHelper.setEnd(now);

        // when
        var result = validator.isValid(testHelper, null);

        // then
        assertTrue(result);
    }

    @Test
    public void ifEndIsNull_thenReturnTrue() {
        // given
        var testHelper = new TestHelper();
        testHelper.setStart(now);

        // when
        var result = validator.isValid(testHelper, null);

        // then
        assertTrue(result);
    }

    @Test
    public void ifStartIsBeforeEnd_thenReturnTrue() {
        // given
        var testHelper = new TestHelper(now, now.plusSeconds(1));

        // when
        var result = validator.isValid(testHelper, null);

        // then
        assertTrue(result);
    }

    @Test
    public void ifStartIsSameAsEnd_thenReturnFalse() {
        // given
        var testHelper = new TestHelper(now, now);

        // when
        var result = validator.isValid(testHelper, null);

        // then
        assertFalse(result);
    }

    @Test
    public void ifEndIsBeforeEnd_thenReturnFalse() {
        // given
        var testHelper = new TestHelper(now, now.minusSeconds(1));

        // when
        var result = validator.isValid(testHelper, null);

        // then
        assertFalse(result);
    }
}