package com.jobhelper.careerflowapi.auth.application;

import com.jobhelper.careerflowapi.auth.application.dto.LoginResult;
import com.jobhelper.careerflowapi.auth.domain.session.AuthSession;
import com.jobhelper.careerflowapi.auth.infrastructure.AuthSessionRepository;
import com.jobhelper.careerflowapi.global.domain.ErrorCode;
import com.jobhelper.careerflowapi.global.exception.AuthorizationException;
import com.jobhelper.careerflowapi.global.exception.BusinessException;
import com.jobhelper.careerflowapi.global.security.SecurityFacade;
import com.jobhelper.careerflowapi.global.security.dto.AuthTokens;
import com.jobhelper.careerflowapi.user.domain.entity.User;
import com.jobhelper.careerflowapi.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthSessionService {

    private final AuthSessionRepository authSessionRepository;
    private final UserRepository userRepository;
    private final SecurityFacade securityFacade;

    public LoginResult createSession(User user) {
        AuthTokens tokens = securityFacade.issueTokens(user.getId(), user.getEmail(), user.getRole());
        AuthSession session = AuthSession.of(user.getEmail(), tokens.refreshToken());
        authSessionRepository.save(session, tokens.refreshTokenTtlSeconds());
        return new LoginResult(tokens.accessToken(), tokens.refreshCookie());
    }

    @Transactional(readOnly = true)
    public LoginResult refresh(String refreshToken) {
        securityFacade.validateToken(refreshToken);

        String email = securityFacade.getEmailFromToken(refreshToken)
                .orElseThrow(() -> new AuthorizationException(ErrorCode.INVALID_REFRESH_TOKEN));

        AuthSession session = authSessionRepository.findByEmail(email)
                .orElseThrow(() -> new AuthorizationException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        if (session.isTheft(refreshToken)) {
            authSessionRepository.delete(email);
            throw new AuthorizationException(ErrorCode.REFRESH_TOKEN_THEFT_DETECTED);
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        AuthTokens tokens = securityFacade.issueTokens(user.getId(), user.getEmail(), user.getRole());
        session.rotate(tokens.refreshToken());
        authSessionRepository.save(session, tokens.refreshTokenTtlSeconds());

        return new LoginResult(tokens.accessToken(), tokens.refreshCookie());
    }

    public ResponseCookie logout(String refreshToken) {
        securityFacade.getEmailFromToken(refreshToken)
                .ifPresent(authSessionRepository::delete);
        return securityFacade.deleteRefreshTokenCookie();
    }
}