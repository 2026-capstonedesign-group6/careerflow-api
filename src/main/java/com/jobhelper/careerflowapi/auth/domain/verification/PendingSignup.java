package com.jobhelper.careerflowapi.auth.domain.verification;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PendingSignup {

    private static final int MAX_ATTEMPTS = 5;
    private static final long TTL_SECONDS = 600L;

    private final String email;
    private final String nickname;
    private final String encodedPassword;
    private final int attemptCount;

    @JsonCreator
    public PendingSignup(
            @JsonProperty("email") String email,
            @JsonProperty("nickname") String nickname,
            @JsonProperty("encodedPassword") String encodedPassword,
            @JsonProperty("attemptCount") int attemptCount
    ) {
        this.email = email;
        this.nickname = nickname;
        this.encodedPassword = encodedPassword;
        this.attemptCount = attemptCount;
    }

    public static PendingSignup of(String email, String nickname, String encodedPassword) {
        return new PendingSignup(email, nickname, encodedPassword, 1);
    }

    public PendingSignup withNewAttempt(String nickname, String encodedPassword) {
        return new PendingSignup(this.email, nickname, encodedPassword, this.attemptCount + 1);
    }

    public boolean isBlocked() {
        return attemptCount > MAX_ATTEMPTS;
    }

    public static long ttlSeconds() {
        return TTL_SECONDS;
    }
}
