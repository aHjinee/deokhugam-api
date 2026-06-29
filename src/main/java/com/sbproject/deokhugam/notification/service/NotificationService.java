package com.sbproject.deokhugam.notification.service;

import com.sbproject.deokhugam.common.dto.SlicePageResponse;
import com.sbproject.deokhugam.notification.dto.NotificationDto;
import com.sbproject.deokhugam.notification.dto.NotificationUpdateRequest;
import com.sbproject.deokhugam.notification.entity.NotificationType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface NotificationService {
    SlicePageResponse<NotificationDto> findAllByUserId(UUID userId, String cursor, Instant after, int limit);

    NotificationDto updateReadStatus(UUID notificationId, UUID deokhugamRequestUserId, NotificationUpdateRequest request);

    void updateReadAllStatus(UUID deokhugamRequestUserId);

    NotificationDto create(NotificationType type,
                UUID receiverId,
                UUID actorId,
                UUID reviewId);
}
