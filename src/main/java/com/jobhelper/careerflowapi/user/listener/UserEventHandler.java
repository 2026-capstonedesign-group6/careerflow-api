package com.jobhelper.careerflowapi.user.listener;

import com.jobhelper.careerflowapi.user.event.UserRegisterEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventHandler {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void onRegister(UserRegisterEvent event) {
        log.info("회원가입 완료 - userId={}, email={}", event.user().getId(), event.user().getEmail());
        // TODO: 기본 프로필 설정 등 사이드이펙트
    }
}
