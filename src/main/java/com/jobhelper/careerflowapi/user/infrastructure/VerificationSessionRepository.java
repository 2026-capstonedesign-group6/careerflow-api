package com.jobhelper.careerflowapi.user.infrastructure;

import com.jobhelper.careerflowapi.user.domain.verification.VerificationSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class VerificationSessionRepository {

    private static final String KEY_PREFIX = "email_verification:";

    private final StringRedisTemplate redisTemplate;

    public void save(VerificationSession session) {
        redisTemplate.opsForValue().set(
                KEY_PREFIX + session.getEmail(),
                session.getCode(),
                Duration.ofSeconds(VerificationSession.ttlSeconds())
        );
    }

    public Optional<VerificationSession> findByEmail(String email) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(KEY_PREFIX + email))
                .map(code -> VerificationSession.of(email, code));
    }

    public void delete(String email) {
        redisTemplate.delete(KEY_PREFIX + email);
    }
}