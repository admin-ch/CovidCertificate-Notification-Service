package ch.admin.bag.covidcertificate.api.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;


@ExtendWith(MockitoExtension.class)
class NotificationDtoTest {

    NotificationDto notificationDto;

    @BeforeEach
    void beforeEach() {
        var messageType = MessageType.INFO;
        var messageDto = new MessageDto("de", "fr", "it", "en");
        var start = LocalDateTime.now().minusHours(1);
        var end = LocalDateTime.now();
        this.notificationDto = new NotificationDto(messageType, messageDto, start, end);
    }

//    @Test
//    void whenValidate_thenOk() {
//        // given
//        // when
//        notificationDto.validate();
//        // then
//    }

//    @ParameterizedTest
//    @CsvSource({
//            "2022-01-02T12:00:01.00,2022-01-02T12:00:00.00",
//            "2022-01-02T12:00:00.00,2022-01-01T12:00:00.00",
//            "2022-01-01T12:00:00.01,2022-01-01T12:00:00.00",
//            "2022-01-01T12:00:01.00,2022-01-01T12:00:00.00",
//            "2022-01-01T12:01:00.00,2022-01-01T12:00:00.00",
//            "2022-03-12T22:30:15.00,2022-03-12T21:15:00.00"
//    })
//    void givenEndIsBeforeStart_whenValidate_thenThrowsNotificationValidationException(LocalDateTime start, LocalDateTime end) {
//        // given
//        this.notificationDto.setStart(start);
//        this.notificationDto.setEnd(end);
//        // when then
//        var exception = assertThrows(NotificationException.class, this.notificationDto::validate);
//        assertEquals(NOTIFICATION_VALIDATION_ERROR, exception.getError());
//
//    }

//    @ParameterizedTest
//    @CsvSource({
//            "2022-01-02T12:00:01.00,2022-01-02T12:00:01.00",
//            "2022-01-02T12:00:00.00,2022-01-02T12:00:00.00",
//            "2022-01-01T12:00:00.01,2022-01-01T12:00:00.01",
//            "2022-01-01T12:00:01.00,2022-01-01T12:00:01.00",
//            "2022-01-01T12:01:00.00,2022-01-01T12:01:00.00",
//            "2022-03-12T22:30:15.00,2022-03-12T22:30:15.00"
//    })
//    void givenStartAndEndIsEqual_whenValidate_thenThrowsNotificationValidationException(LocalDateTime start, LocalDateTime end) {
//        // given
//        this.notificationDto.setStart(start);
//        this.notificationDto.setEnd(end);
//        // when then
//        var exception = assertThrows(NotificationException.class, this.notificationDto::validate);
//        assertEquals(NOTIFICATION_VALIDATION_ERROR, exception.getError());
//    }
}