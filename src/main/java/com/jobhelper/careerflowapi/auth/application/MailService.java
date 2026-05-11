package com.jobhelper.careerflowapi.auth.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Async
    public void sendVerificationCode(String to, String nickname, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("[Careerflow] 이메일 인증 코드");
        message.setText("""
                안녕하세요, %s님!

                Careerflow 회원가입을 완료하려면 아래 인증 코드를 입력해 주세요.

                인증 코드: %s

                유효 시간: 10분

                본인이 요청하지 않은 경우 이 메일을 무시하세요.
                """.formatted(nickname, code));

        try {
            mailSender.send(message);
            log.info("인증 이메일 발송 완료 - to={}", to);
        } catch (Exception e) {
            log.error("인증 이메일 발송 실패 - to={}", to, e);
        }
    }
}