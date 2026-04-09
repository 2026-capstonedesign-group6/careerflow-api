package com.jobhelper.careerflowapi.auth.strategy;

import com.jobhelper.careerflowapi.auth.application.AuthSessionService;
import com.jobhelper.careerflowapi.auth.application.VerificationService;
import com.jobhelper.careerflowapi.auth.application.dto.LoginResult;
import com.jobhelper.careerflowapi.auth.domain.verification.PendingSignup;
import com.jobhelper.careerflowapi.auth.event.LoginFailedEvent;
import com.jobhelper.careerflowapi.auth.infrastructure.PendingSignupRepository;
import com.jobhelper.careerflowapi.auth.presentation.dto.request.LocalLoginRequest;
import com.jobhelper.careerflowapi.auth.presentation.dto.request.LocalSignupRequest;
import com.jobhelper.careerflowapi.global.domain.ErrorCode;
import com.jobhelper.careerflowapi.global.exception.BusinessException;
import com.jobhelper.careerflowapi.user.domain.entity.User;
import com.jobhelper.careerflowapi.user.domain.entity.UserAccount;
import com.jobhelper.careerflowapi.user.domain.enums.Provider;
import com.jobhelper.careerflowapi.user.infrastructure.UserAccountRepository;
import com.jobhelper.careerflowapi.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailAccountStrategy implements AccountStrategy {

    private final UserRepository userRepository;
    private final UserAccountRepository userAccountRepository;
    private final PendingSignupRepository pendingSignupRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;
    private final VerificationService verificationService;
    private final AuthSessionService authSessionService;

    public void register(LocalSignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        Optional<PendingSignup> existing = pendingSignupRepository.findByEmail(request.email());
        PendingSignup pending;

        if (existing.isPresent()) {
            pending = existing.get().withNewAttempt(request.nickname(), encodedPassword);
            pendingSignupRepository.save(pending);
            if (pending.isBlocked()) {
                throw new BusinessException(ErrorCode.TOO_MANY_REQUESTS);
            }
        } else {
            pending = PendingSignup.of(request.email(), request.nickname(), encodedPassword);
            pendingSignupRepository.save(pending);
        }

        verificationService.sendVerificationEmail(request.email(), request.nickname());
    }

    @Transactional(readOnly = true)
    public LoginResult authenticate(LocalLoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> {
                    eventPublisher.publishEvent(new LoginFailedEvent(request.email(), "존재하지 않는 이메일"));
                    return new BusinessException(ErrorCode.USER_NOT_FOUND, "이메일에 해당하는 사용자를 찾을 수 없습니다.");
                });

        UserAccount account = userAccountRepository.findByUserAndProvider(user, Provider.LOCAL)
                .orElseThrow(() -> {
                    eventPublisher.publishEvent(new LoginFailedEvent(request.email(), "로컬 계정 없음"));
                    return new BusinessException(ErrorCode.USER_NOT_FOUND, "로컬 계정을 찾을 수 없습니다. 가입하신 소셜 로그인으로 진행해 주세요.");
                });

        if (!passwordEncoder.matches(request.password(), account.getPassword())) {
            eventPublisher.publishEvent(new LoginFailedEvent(request.email(), "비밀번호 불일치"));
            throw new BusinessException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        return authSessionService.createSession(user);
    }
}
