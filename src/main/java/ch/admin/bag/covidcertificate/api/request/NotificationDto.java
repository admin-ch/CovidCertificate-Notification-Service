package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.exception.NotificationValidationException;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static ch.admin.bag.covidcertificate.api.Constants.NOTIFICATION_END_BEFORE_START_ERROR;
import static ch.admin.bag.covidcertificate.api.Constants.NOTIFICATION_SAME_END_AND_START_TIME_ERROR;
import static ch.admin.bag.covidcertificate.api.error.RestError.restValidationError;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Slf4j
public class NotificationDto {

    @NotNull
    MessageType type;

    @NotNull
    @Valid
    MessageDto message;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "UTC")
    @NotNull
    private LocalDateTime start;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "UTC")
    @NotNull
    private LocalDateTime end;

    public void validate() {
        if (this.end.isBefore(this.start)) {
            throw new NotificationValidationException(restValidationError(NOTIFICATION_END_BEFORE_START_ERROR));
        } else if (this.start.isEqual(this.end)) {
            throw new NotificationValidationException(restValidationError(NOTIFICATION_SAME_END_AND_START_TIME_ERROR));
        }
    }
}
