package com.jobhelper.careerflowapi.user.application;

import com.jobhelper.careerflowapi.global.domain.ErrorCode;
import com.jobhelper.careerflowapi.global.exception.BusinessException;
import com.jobhelper.careerflowapi.user.domain.verification.PendingSignup;
import com.jobhelper.careerflowapi.user.domain.verification.VerificationSession;
import com.jobhelper.careerflowapi.user.infrastructure.PendingSignupRepository;
import com.jobhelper.careerflowapi.user.infrastructure.UserRepository;
import com.jobhelper.careerflowapi.user.infrastructure.VerificationSessionRepository;
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
    private final UserCreationService userCreationService;

    private static final SecureRandom RANDOM = new SecureRandom();

    public void sendVerificationEmail(String email, String nickname) {
        String code = generateCode();
        verificationSessionRepository.save(VerificationSession.of(email, code));
        mailService.sendVerificationCode(email, nickname, code);
    }

    public void verify(String email, String code) {
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

        userCreationService.createUser(pending);

        verificationSessionRepository.delete(email);
        pendingSignupRepository.delete(email);
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
