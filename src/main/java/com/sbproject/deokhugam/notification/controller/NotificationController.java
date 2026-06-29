package com.sbproject.deokhugam.notification.controller;

import com.sbproject.deokhugam.common.dto.SlicePageResponse;
import com.sbproject.deokhugam.notification.dto.NotificationDto;
import com.sbproject.deokhugam.notification.entity.NotificationType;
import com.sbproject.deokhugam.notification.service.NotificationService;
import com.sbproject.deokhugam.user.service.UserService;
import com.sbproject.deokhugam.user.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<SlicePageResponse<NotificationDto>> findAll(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) Instant after,
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(notificationService.findAllByUserId(userId, cursor, after, limit));
    }

}
