package ch.admin.bag.covidcertificate.api.mapper;

import ch.admin.bag.covidcertificate.api.Constants;
import ch.admin.bag.covidcertificate.api.exception.NotificationException;
import ch.admin.bag.covidcertificate.api.request.NotificationContentDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationContentMapper {

    public static NotificationContentDto fromNotificationContent(String content) {
        final var mapper = new ObjectMapper();
        try {
            return mapper.readValue(content, NotificationContentDto.class);
        } catch (JsonProcessingException e) {
            throw new NotificationException(Constants.NOTIFICATION_MAPPING_ERROR);
        }
    }

    public static String fromNotificationContentDto(NotificationContentDto content) {
        final var mapper = new ObjectMapper();
        try {
            return mapper.writer().writeValueAsString(content);
        } catch (JsonProcessingException e) {
            throw new NotificationException(Constants.NOTIFICATION_MAPPING_ERROR);
        }
    }
}
