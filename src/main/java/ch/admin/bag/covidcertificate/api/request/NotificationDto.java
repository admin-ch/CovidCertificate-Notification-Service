package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.validation.StartBeforeEnd;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static ch.admin.bag.covidcertificate.api.Constants.CONTENT_MUST_NOT_BE_NULL;
import static ch.admin.bag.covidcertificate.api.Constants.IS_CLOSABLE_MUST_NOT_BE_NULL;
import static ch.admin.bag.covidcertificate.api.Constants.START_HAS_TO_BE_BEFORE_END;
import static ch.admin.bag.covidcertificate.api.Constants.TYPE_MUST_NOT_BE_NULL;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Slf4j
@SuperBuilder
@StartBeforeEnd(message = START_HAS_TO_BE_BEFORE_END)
public abstract class NotificationDto implements HasStartEnd {

    @NotNull(message = TYPE_MUST_NOT_BE_NULL)
    NotificationType type;

    @NotNull(message = CONTENT_MUST_NOT_BE_NULL)
    @Valid
    NotificationContentDto content;

    @NotNull(message = IS_CLOSABLE_MUST_NOT_BE_NULL)
    @JsonProperty("isClosable")
    boolean isClosable;
}
