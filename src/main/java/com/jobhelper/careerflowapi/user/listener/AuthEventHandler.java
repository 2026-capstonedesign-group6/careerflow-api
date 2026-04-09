package com.jobhelper.careerflowapi.user.listener;

import com.jobhelper.careerflowapi.global.security.SecurityFacade;
import com.jobhelper.careerflowapi.global.security.dto.AuthTokens;
import com.jobhelper.careerflowapi.user.application.LoginResult;
import com.jobhelper.careerflowapi.user.domain.session.AuthSession;
import com.jobhelper.careerflowapi.user.domain.entity.User;
import com.jobhelper.careerflowapi.user.event.LoginEvent;
import com.jobhelper.careerflowapi.user.infrastructure.AuthSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthEventHandler {

    private final SecurityFacade securityFacade;
    private final AuthSessionRepository authSessionRepository;

    @EventListener
    public void onLogin(LoginEvent event) {
        User user = event.getUser();

        AuthTokens tokens = securityFacade.issueTokens(user.getId(), user.getEmail(), user.getRole());

        AuthSession session = AuthSession.of(user.getEmail(), tokens.refreshToken());
        authSessionRepository.save(session, tokens.refreshTokenTtlSeconds());

        event.complete(new LoginResult(tokens.accessToken(), tokens.refreshCookie()));
    }
}
