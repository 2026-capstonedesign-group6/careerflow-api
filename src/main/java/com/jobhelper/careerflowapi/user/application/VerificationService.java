package com.jobhelper.careerflowapi.user.application;

import com.jobhelper.careerflowapi.global.domain.ErrorCode;
import com.jobhelper.careerflowapi.global.exception.BusinessException;
import com.jobhelper.careerflowapi.user.domain.entity.User;
import com.jobhelper.careerflowapi.user.domain.verification.VerificationSession;
import com.jobhelper.careerflowapi.user.infrastructure.UserRepository;
import com.jobhelper.careerflowapi.user.infrastructure.VerificationSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final VerificationSessionRepository verificationSessionRepository;
    private final UserRepository userRepository;
    private final MailService mailService;

    private static final SecureRandom RANDOM = new SecureRandom();

    public void sendVerificationEmail(String email, String nickname) {
        String code = generateCode();
        VerificationSession session = VerificationSession.of(email, code);
        verificationSessionRepository.save(session);
        mailService.sendVerificationCode(email, nickname, code);
    }

    @Transactional
    public void verify(String email, String code) {
        VerificationSession session = verificationSessionRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.VERIFICATION_EXPIRED));

        if (!session.matches(code)) {
            throw new BusinessException(ErrorCode.VERIFICATION_CODE_MISMATCH);
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (user.isEmailVerified()) {
            throw new BusinessException(ErrorCode.VERIFICATION_ALREADY_COMPLETED);
        }

        user.verifyEmail();
        verificationSessionRepository.delete(email);
    }

    @Transactional(readOnly = true)
    public void resend(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (user.isEmailVerified()) {
            throw new BusinessException(ErrorCode.VERIFICATION_ALREADY_COMPLETED);
        }

        sendVerificationEmail(email, user.getNickname());
    }

    private String generateCode() {
        return String.format("%06d", RANDOM.nextInt(1_000_000));
    }
}
