package com.jobhelper.careerflowapi.auth.application;

import com.jobhelper.careerflowapi.auth.event.LinkFactorEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountLinkingService {

    @EventListener
    @Async
    public void onLinkFactor(LinkFactorEvent event) {
        log.info("멀티팩터 연동 요청 - userId={}, provider={}", event.userId(), event.provider());
        // TODO: 소셜 계정 연동 처리 구현 예정
    }
}