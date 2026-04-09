package com.jobhelper.careerflowapi.user.listener;

import com.jobhelper.careerflowapi.user.application.VerificationService;
import com.jobhelper.careerflowapi.user.event.LoginFailedEvent;
import com.jobhelper.careerflowapi.user.event.UserRegisterEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventHandler {

    private final VerificationService verificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void onRegister(UserRegisterEvent event) {
        log.info("회원가입 완료 - userId={}, email={}", event.user().getId(), event.user().getEmail());
        verificationService.sendVerificationEmail(event.user().getEmail(), event.user().getNickname());
    }

    @EventListener
    @Async
    public void onLoginFailed(LoginFailedEvent event) {
        log.warn("로그인 실패 - email={}, reason={}", event.email(), event.reason());
        // TODO: 연속 실패 카운터, 계정 잠금(F5, F6) 처리
    }
}
