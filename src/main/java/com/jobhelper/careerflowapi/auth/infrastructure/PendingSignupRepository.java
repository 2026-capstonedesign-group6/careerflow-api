package com.jobhelper.careerflowapi.auth.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobhelper.careerflowapi.auth.domain.verification.PendingSignup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PendingSignupRepository {

    private static final String KEY_PREFIX = "pending_signup:";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public void save(PendingSignup pending) {
        try {
            String json = objectMapper.writeValueAsString(pending);
            redisTemplate.opsForValue().set(
                    KEY_PREFIX + pending.getEmail(),
                    json,
                    Duration.ofSeconds(PendingSignup.ttlSeconds())
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("PendingSignup 직렬화 실패", e);
        }
    }

    public Optional<PendingSignup> findByEmail(String email) {
        String json = redisTemplate.opsForValue().get(KEY_PREFIX + email);
        if (json == null) return Optional.empty();
        try {
            log.debug("[Redis] PendingSignup GET key={}, json={}", KEY_PREFIX + email, json);
            return Optional.of(objectMapper.readValue(json, PendingSignup.class));
        } catch (JsonProcessingException e) {
            log.error("[Redis] PendingSignup 역직렬화 실패 - key={}, json={}, error={}", KEY_PREFIX + email, json, e.getMessage());
            return Optional.empty();
        }
    }

    public void delete(String email) {
        redisTemplate.delete(KEY_PREFIX + email);
    }
}
