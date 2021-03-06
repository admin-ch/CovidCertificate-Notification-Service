package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.validation.NotInPast;
import ch.admin.bag.covidcertificate.api.validation.StartBeforeEnd;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static ch.admin.bag.covidcertificate.api.Constants.END_MUST_NOT_BE_IN_PAST;
import static ch.admin.bag.covidcertificate.api.Constants.END_MUST_NOT_BE_NULL;
import static ch.admin.bag.covidcertificate.api.Constants.MESSAGE_MUST_NOT_BE_NULL;
import static ch.admin.bag.covidcertificate.api.Constants.MESSAGE_TYPE_MUST_NOT_BE_NULL;
import static ch.admin.bag.covidcertificate.api.Constants.START_HAS_TO_BE_BEFORE_END;
import static ch.admin.bag.covidcertificate.api.Constants.START_MUST_NOT_BE_NULL;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Slf4j
@StartBeforeEnd(message = START_HAS_TO_BE_BEFORE_END)
public class NotificationDto {

    @NotNull(message = MESSAGE_TYPE_MUST_NOT_BE_NULL)
    MessageType type;

    @NotNull(message = MESSAGE_MUST_NOT_BE_NULL)
    @Valid
    MessageDto message;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "UTC")
    @NotNull(message = START_MUST_NOT_BE_NULL)
    private LocalDateTime start;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "UTC")
    @NotNull(message = END_MUST_NOT_BE_NULL)
    @NotInPast(message = END_MUST_NOT_BE_IN_PAST)
    private LocalDateTime end;
}
