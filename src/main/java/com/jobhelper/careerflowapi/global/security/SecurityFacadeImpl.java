package com.jobhelper.careerflowapi.global.security;

import com.jobhelper.careerflowapi.global.domain.ErrorCode;
import com.jobhelper.careerflowapi.global.exception.AuthorizationException;
import com.jobhelper.careerflowapi.global.security.dto.AuthTokens;
import com.jobhelper.careerflowapi.global.security.dto.AuthUser;
import com.jobhelper.careerflowapi.global.security.jwt.JwtProperties;
import com.jobhelper.careerflowapi.global.security.jwt.JwtTokenProvider;
import com.jobhelper.careerflowapi.global.security.jwt.JwtTokenValidator;
import com.jobhelper.careerflowapi.global.security.jwt.TokenCookieFactory;
import com.jobhelper.careerflowapi.global.security.principal.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SecurityFacadeImpl implements SecurityFacade {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenValidator jwtTokenValidator;
    private final JwtProperties jwtProperties;
    private final TokenCookieFactory tokenCookieFactory;

    @Override
    public AuthTokens issueTokens(Long userId, String email, String role) {
        var authorities = List.of(new SimpleGrantedAuthority(role));

        String accessToken = jwtTokenProvider.createAccessToken(userId, email, authorities);
        String refreshToken = jwtTokenProvider.createRefreshToken(email);
        long ttl = jwtProperties.refreshTokenValidityInSeconds();
        ResponseCookie refreshCookie = tokenCookieFactory.createRefreshTokenCookie(refreshToken);

        return new AuthTokens(accessToken, refreshToken, ttl, refreshCookie);
    }

    @Override
    public void validateToken(String token) {
        jwtTokenValidator.validateToken(token);
    }

    @Override
    public Optional<String> getEmailFromToken(String token) {
        return jwtTokenValidator.getEmailFromRefreshToken(token);
    }

    @Override
    public ResponseCookie deleteRefreshTokenCookie() {
        return tokenCookieFactory.deleteRefreshTokenCookie();
    }

    @Override
    public AuthUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthorizationException(ErrorCode.UNAUTHORIZED);
        }
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return principal.getAuthUser();
    }
}
