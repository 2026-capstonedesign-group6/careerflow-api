package com.jobhelper.careerflowapi.auth.infrastructure;

import com.jobhelper.careerflowapi.auth.domain.session.AuthSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AuthSessionRepository {

    private static final String KEY_PREFIX = "refresh:";

    private final StringRedisTemplate redisTemplate;

    public void save(AuthSession session, long ttlSeconds) {
        redisTemplate.opsForValue().set(KEY_PREFIX + session.getEmail(), session.getRefreshToken(), Duration.ofSeconds(ttlSeconds));
    }

    public Optional<AuthSession> findByEmail(String email) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(KEY_PREFIX + email))
                .map(token -> AuthSession.of(email, token));
    }

    public void delete(String email) {
        redisTemplate.delete(KEY_PREFIX + email);
    }
}
