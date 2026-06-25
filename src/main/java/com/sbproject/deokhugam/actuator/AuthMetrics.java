package com.sbproject.deokhugam.actuator;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 로그인 시도/성공 횟수, 현재 활성 사용자 수를 Micrometer로 측정
 * → /actuator/metrics/auth.login.attempts 등으로 노출
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthMetrics {

    private final MeterRegistry meterRegistry;

    private Counter loginAttemptCounter; // 전체 시도 수
    private Counter loginSuccessCounter; // 성공 수

    // 활성 사용자 (username → 더미값)
    private final Map<String, Long> activeUserMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        loginAttemptCounter = Counter.builder("auth.login.attempts")
                .description("로그인 시도 횟수")
                .tag("system", "blog")
                .register(meterRegistry);

        loginSuccessCounter = Counter.builder("auth.login.success")
                .description("로그인 성공 횟수")
                .tag("system", "blog")
                .register(meterRegistry);

        // 활성 사용자 수 게이지 (Map 크기 실시간 반영)
        Gauge.builder("auth.active.user.count", activeUserMap, Map::size)
                .description("현재 활성 사용자 수")
                .tag("system", "blog")
                .register(meterRegistry);
    }

    public void incrementLoginAttempt() {
        loginAttemptCounter.increment();
    }

    public void onLoginSuccess(String username) {
        loginSuccessCounter.increment();
        activeUserMap.put(username, 1L);
    }

    public void removeActiveUser(String username) {
        activeUserMap.remove(username);
    }

    public int currentUserCount() {
        return activeUserMap.size();
    }
}
