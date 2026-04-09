package com.jobhelper.careerflowapi.global.security;

import com.jobhelper.careerflowapi.global.security.dto.AuthTokens;
import com.jobhelper.careerflowapi.global.security.dto.AuthUser;
import org.springframework.http.ResponseCookie;

import java.util.Optional;

public interface SecurityFacade {

    AuthTokens issueTokens(Long userId, String email, String role);

    void validateToken(String token);

    Optional<String> getEmailFromToken(String token);

    ResponseCookie deleteRefreshTokenCookie();

    AuthUser getCurrentUser();
}
