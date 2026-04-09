package com.jobhelper.careerflowapi.user.infrastructure;

import com.jobhelper.careerflowapi.user.domain.verification.VerificationSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class VerificationSessionRepository {

    private static final String KEY_PREFIX = "email_verification:";

    private final StringRedisTemplate redisTemplate;

    public void save(VerificationSession session) {
        String key = KEY_PREFIX + session.getEmail();
        redisTemplate.opsForValue().set(key, session.getCode(), Duration.ofSeconds(VerificationSession.ttlSeconds()));
        log.debug("[Redis] SAVE key={}, value={}", key, session.getCode());
    }

    public Optional<VerificationSession> findByEmail(String email) {
        String key = KEY_PREFIX + email;
        String value = redisTemplate.opsForValue().get(key);
        log.debug("[Redis] GET key={}, value={}", key, value);
        return Optional.ofNullable(value)
                .map(code -> VerificationSession.of(email, code));
    }

    public void delete(String email) {
        redisTemplate.delete(KEY_PREFIX + email);
    }
}
