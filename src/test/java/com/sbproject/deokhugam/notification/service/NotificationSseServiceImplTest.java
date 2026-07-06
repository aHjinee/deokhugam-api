package com.sbproject.deokhugam.notification.service;

import com.sbproject.deokhugam.notification.repository.SseEmitterRepository;
import com.sbproject.deokhugam.notification.service.impl.NotificationSseServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class NotificationSseServiceImplTest {

    @Mock
    private SseEmitterRepository sseEmitterRepository;

    @InjectMocks
    private NotificationSseServiceImpl notificationSseService;

    @Test
    @DisplayName("SSE 연결")
    void connect() {

        UUID userId = UUID.randomUUID();

        SseEmitter emitter = notificationSseService.connect(userId);

        assertThat(emitter).isNotNull();

        then(sseEmitterRepository)
                .should()
                .save(eq(userId), any(SseEmitter.class));
    }

    @Test
    @DisplayName("알림 전송 성공")
    void notify_success() {

        UUID userId = UUID.randomUUID();
        SseEmitter emitter = new SseEmitter();

        given(sseEmitterRepository.get(userId))
                .willReturn(emitter);

        notificationSseService.notify(userId);

        then(sseEmitterRepository)
                .should()
                .get(userId);

        then(sseEmitterRepository)
                .should(never())
                .delete(any());
    }

    @Test
    @DisplayName("연결된 emitter가 없으면 종료")
    void notify_noEmitter() {

        UUID userId = UUID.randomUUID();

        given(sseEmitterRepository.get(userId))
                .willReturn(null);

        notificationSseService.notify(userId);

        then(sseEmitterRepository)
                .should()
                .get(userId);

        then(sseEmitterRepository)
                .should(never())
                .delete(any());
    }
}