package com.sbproject.deokhugam.notification.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

public interface NotificationSseService {

    SseEmitter connect(UUID userId);

    void notify(UUID userId);
}