package com.jobhelper.careerflowapi.user.domain.verification;

import lombok.Getter;

@Getter
public class VerificationSession {

    private static final long TTL_SECONDS = 600L; // 10분

    private final String email;
    private final String code;

    private VerificationSession(String email, String code) {
        this.email = email;
        this.code = code;
    }

    public static VerificationSession of(String email, String code) {
        return new VerificationSession(email, code);
    }

    public boolean matches(String inputCode) {
        return this.code.equals(inputCode);
    }

    public static long ttlSeconds() {
        return TTL_SECONDS;
    }
}
