package ch.admin.bag.covidcertificate.api.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

import static ch.admin.bag.covidcertificate.api.Constants.END_MUST_NOT_BE_NULL;
import static ch.admin.bag.covidcertificate.api.Constants.ID_MUST_NOT_BE_NULL;
import static ch.admin.bag.covidcertificate.api.Constants.START_MUST_NOT_BE_NULL;

@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class EditNotificationDto extends NotificationDto implements HasStartEnd {

    @NotNull(message = ID_MUST_NOT_BE_NULL)
    @Getter
    @Setter
    UUID id;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @NotNull(message = START_MUST_NOT_BE_NULL)
    private LocalDateTime startTime;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @NotNull(message = END_MUST_NOT_BE_NULL)
    private LocalDateTime endTime;

    // CAUTION: We must NOT use @Getter, @Setter oder @Data since there is a bug in Lombok inheriting the validations
    //  see: https://github.com/projectlombok/lombok/issues/3180
    // This will only throw an error at runtime, NOT already in compile time, so be careful.
    @Override
    public LocalDateTime getStartTime() {
        return this.startTime;
    }

    @Override
    public void setStartTime(LocalDateTime localDateTime) {
        this.startTime = localDateTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    @Override
    public void setEndTime(LocalDateTime localDateTime) {
        this.endTime = localDateTime;
    }
}
