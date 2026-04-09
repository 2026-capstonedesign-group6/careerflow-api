package com.jobhelper.careerflowapi.global.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenCookieFactory {

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    private final JwtProperties jwtProperties;

    public ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(jwtProperties.cookieSecure())
                .path("/api/auth/refresh")
                .maxAge(jwtProperties.refreshTokenValidityInSeconds())
                .sameSite("None")
                .build();
    }

     public ResponseCookie deleteRefreshTokenCookie() {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(jwtProperties.cookieSecure())
                .path("/api/auth/refresh")
                .maxAge(0)
                .sameSite("None")
                .build();
    }
}
