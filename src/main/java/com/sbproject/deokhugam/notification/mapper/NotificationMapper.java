package com.sbproject.deokhugam.notification.mapper;

import com.sbproject.deokhugam.notification.dto.NotificationDto;
import com.sbproject.deokhugam.notification.dto.NotificationUpdateRequest;
import com.sbproject.deokhugam.notification.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "review.id", target = "reviewId")
    NotificationDto toDto(Notification notification);

    void update(NotificationUpdateRequest request,
                @MappingTarget Notification notification);
}