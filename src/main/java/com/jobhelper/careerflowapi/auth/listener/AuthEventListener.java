package com.jobhelper.careerflowapi.auth.listener;

import com.jobhelper.careerflowapi.auth.event.LoginFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthEventListener {

    @EventListener
    @Async
    public void onLoginFailed(LoginFailedEvent event) {
        log.warn("로그인 실패 - email={}, reason={}", event.email(), event.reason());
        // TODO: 연속 실패 카운터, 계정 잠금(F5, F6) 처리
    }
}
