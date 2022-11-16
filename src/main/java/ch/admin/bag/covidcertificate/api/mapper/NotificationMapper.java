package ch.admin.bag.covidcertificate.api.mapper;

import ch.admin.bag.covidcertificate.api.request.EditNotificationDto;
import ch.admin.bag.covidcertificate.api.request.NotificationDto;
import ch.admin.bag.covidcertificate.domain.Notification;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Component
public class NotificationMapper {

    public EditNotificationDto fromEntity(Notification entity) {
        return EditNotificationDto.builder()
                .id(entity.getId())
                .type(entity.getType())
                .content(NotificationContentMapper.fromNotificationContent(entity.getContent()))
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .isClosable(entity.isClosable())
                .build();
    }

    public Notification fromDto(EditNotificationDto dto) {
        return Notification.builder()
                .id(dto.getId())
                .type(dto.getType())
                .content(NotificationContentMapper.fromNotificationContentDto(dto.getContent()))
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .isClosable(dto.isClosable())
                .build();
    }

    public Notification fromDto(NotificationDto dto) {
        return Notification.builder()
                .type(dto.getType())
                .content(NotificationContentMapper.fromNotificationContentDto(dto.getContent()))
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .isClosable(dto.isClosable())
                .build();
    }

    public List<EditNotificationDto> fromEntity(List<Notification> entities) {
        return entities.stream().map(this::fromEntity).collect(Collectors.toList());
    }
}