package com.sookmyung.swapclass.infra.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class AuthCodeStore {

    private final StringRedisTemplate redisTemplate;

    private static final String CODE_PREFIX = "auth:code:";
    private static final String VERIFIED_PREFIX = "auth:verified:";
    private static final Duration CODE_TTL = Duration.ofMinutes(5);
    private static final Duration VERIFIED_TTL = Duration.ofMinutes(30);

    // --- 인증코드 (TTL 5분) ---

    public void saveCode(String email, String code) {
        redisTemplate.opsForValue().set(CODE_PREFIX + email, code, CODE_TTL);
    }

    public String getCode(String email) {
        return redisTemplate.opsForValue().get(CODE_PREFIX + email);
    }

    public void deleteCode(String email) {
        redisTemplate.delete(CODE_PREFIX + email);
    }

    // --- 인증완료 플래그 (TTL 30분) ---

    public void markVerified(String email) {
        redisTemplate.opsForValue().set(VERIFIED_PREFIX + email, "true", VERIFIED_TTL);
    }

    public boolean isVerified(String email) {
        return Boolean.parseBoolean(redisTemplate.opsForValue().get(VERIFIED_PREFIX + email));
    }

    public void deleteVerified(String email) {
        redisTemplate.delete(VERIFIED_PREFIX + email);
    }
}
