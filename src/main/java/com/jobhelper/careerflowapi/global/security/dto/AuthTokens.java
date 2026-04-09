package com.jobhelper.careerflowapi.global.security.dto;

import org.springframework.http.ResponseCookie;

public record AuthTokens(
        String accessToken,
        String refreshToken,
        long refreshTokenTtlSeconds,
        ResponseCookie refreshCookie
) {
}
