package com.sbproject.deokhugam.notification.controller;

import com.sbproject.deokhugam.common.dto.SlicePageResponse;
import com.sbproject.deokhugam.notification.dto.NotificationDto;
import com.sbproject.deokhugam.notification.dto.NotificationUpdateRequest;
import com.sbproject.deokhugam.notification.service.NotificationService;
import com.sbproject.deokhugam.notification.service.NotificationSseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    private final NotificationSseService notificationSseService;

    @GetMapping
    public ResponseEntity<SlicePageResponse<NotificationDto>> findAll(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) Instant after,
            @RequestParam(defaultValue = "20") int limit) {
        log.info("Notification 조회");
        return ResponseEntity.ok(notificationService.findAllByUserId(userId, cursor, after, limit));
    }

    @PatchMapping("/{notificationId}")
    public ResponseEntity<NotificationDto> updateReadStatus(
            @PathVariable UUID notificationId,
            @RequestHeader(value = "Deokhugam-Request-User-ID", required = false) UUID deokhugamRequestUserId,
            @RequestBody NotificationUpdateRequest request
    ) {
        return ResponseEntity.ok(notificationService.updateReadStatus(notificationId, deokhugamRequestUserId, request));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> updateReadAllStatus(
            @RequestHeader(value = "Deokhugam-Request-User-ID", required = false) UUID deokhugamRequestUserId
    ) {
        notificationService.updateReadAllStatus(deokhugamRequestUserId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(
            @RequestHeader("Deokhugam-Request-User-ID") UUID userId
    ) {
        return notificationSseService.connect(userId);
    }

}
