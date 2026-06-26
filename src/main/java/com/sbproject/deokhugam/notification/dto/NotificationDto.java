package com.sbproject.deokhugam.notification.dto;

import com.sbproject.deokhugam.notification.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDto {
    private UUID id;
    private UUID userId;
    private UUID reviewId;
    private String reviewContent;
    private String message;
    private boolean confirmed;
    private Instant createdAt;
    private Instant updatedAt;
}
