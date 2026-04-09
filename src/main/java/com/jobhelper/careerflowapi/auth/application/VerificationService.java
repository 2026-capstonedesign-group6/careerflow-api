package com.jobhelper.careerflowapi.auth.application;

import com.jobhelper.careerflowapi.auth.domain.verification.PendingSignup;
import com.jobhelper.careerflowapi.auth.domain.verification.VerificationSession;
import com.jobhelper.careerflowapi.auth.infrastructure.PendingSignupRepository;
import com.jobhelper.careerflowapi.auth.infrastructure.VerificationSessionRepository;
import com.jobhelper.careerflowapi.global.domain.ErrorCode;
import com.jobhelper.careerflowapi.global.exception.BusinessException;
import com.jobhelper.careerflowapi.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final VerificationSessionRepository verificationSessionRepository;
    private final PendingSignupRepository pendingSignupRepository;
    private final UserRepository userRepository;
    private final MailService mailService;

    private static final SecureRandom RANDOM = new SecureRandom();

    public void sendVerificationEmail(String email, String nickname) {
        String code = generateCode();
        verificationSessionRepository.save(VerificationSession.of(email, code));
        mailService.sendVerificationCode(email, nickname, code);
    }

    public PendingSignup verify(String email, String code) {
        VerificationSession session = verificationSessionRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.VERIFICATION_EXPIRED));

        if (!session.matches(code)) {
            throw new BusinessException(ErrorCode.VERIFICATION_CODE_MISMATCH);
        }

        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.VERIFICATION_ALREADY_COMPLETED);
        }

        PendingSignup pending = pendingSignupRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.VERIFICATION_EXPIRED));

        verificationSessionRepository.delete(email);
        pendingSignupRepository.delete(email);

        return pending;
    }

    public void resend(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.VERIFICATION_ALREADY_COMPLETED);
        }

        PendingSignup pending = pendingSignupRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.VERIFICATION_NOT_FOUND));

        sendVerificationEmail(email, pending.getNickname());
    }

    private String generateCode() {
        return String.format("%06d", RANDOM.nextInt(1_000_000));
    }
}