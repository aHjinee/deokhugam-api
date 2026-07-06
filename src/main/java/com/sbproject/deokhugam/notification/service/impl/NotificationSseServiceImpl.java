package com.sbproject.deokhugam.notification.service.impl;

import com.sbproject.deokhugam.notification.repository.SseEmitterRepository;
import com.sbproject.deokhugam.notification.service.NotificationSseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationSseServiceImpl implements NotificationSseService {

    private static final Long DEFAULT_TIMEOUT = 60L * 60 * 1000; // 1시간

    private final SseEmitterRepository sseEmitterRepository;

    @Override
    public SseEmitter connect(UUID userId) {

        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        sseEmitterRepository.save(userId, emitter);

        emitter.onCompletion(() -> {
            sseEmitterRepository.delete(userId);
        });

        emitter.onTimeout(() -> {
            sseEmitterRepository.delete(userId);
            emitter.complete();
        });

        emitter.onError(e -> {
            sseEmitterRepository.delete(userId);
        });

        try {
            emitter.send(
                    SseEmitter.event()
                            .name("connect")
                            .data("connected")
            );
        } catch (IOException e) {
            emitter.completeWithError(e);
        }

        return emitter;
    }

    @Override
    public void notify(UUID userId) {

        SseEmitter emitter = sseEmitterRepository.get(userId);

        if (emitter == null) {
            return;
        }

        try {
            emitter.send(
                    SseEmitter.event()
                            .name("notification")
                            .data("NEW_NOTIFICATION")
            );
        } catch (IOException e) {
            sseEmitterRepository.delete(userId);
            emitter.completeWithError(e);
        }
    }
}