package com.sookmyung.swapclass.infra.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RefreshTokenStore {

    private final StringRedisTemplate redisTemplate;

    private static final String REFRESH_PREFIX = "auth:refresh:";
    private static final Duration REFRESH_TTL = Duration.ofDays(14);

    // userId -> refreshToken (한 기기 로그인: 새로 저장하면 이전 토큰 덮어씀)
    public void save(Long userId, String refreshToken) {
        redisTemplate.opsForValue().set(REFRESH_PREFIX + userId, refreshToken, REFRESH_TTL);
    }

    public String get(Long userId) {
        return redisTemplate.opsForValue().get(REFRESH_PREFIX + userId);
    }

    public void delete(Long userId) {
        redisTemplate.delete(REFRESH_PREFIX + userId);
    }
}
