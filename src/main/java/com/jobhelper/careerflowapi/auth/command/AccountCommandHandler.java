package com.jobhelper.careerflowapi.auth.command;

import com.jobhelper.careerflowapi.auth.application.AuthSessionService;
import com.jobhelper.careerflowapi.auth.application.VerificationService;
import com.jobhelper.careerflowapi.auth.application.dto.LoginResult;
import com.jobhelper.careerflowapi.auth.domain.verification.PendingSignup;
import com.jobhelper.careerflowapi.auth.presentation.dto.request.LocalLoginRequest;
import com.jobhelper.careerflowapi.auth.presentation.dto.request.LocalSignupRequest;
import com.jobhelper.careerflowapi.auth.strategy.EmailAccountStrategy;
import com.jobhelper.careerflowapi.auth.strategy.SocialAccountStrategy;
import com.jobhelper.careerflowapi.global.domain.ErrorCode;
import com.jobhelper.careerflowapi.global.exception.BusinessException;
import com.jobhelper.careerflowapi.user.application.UserCreationService;
import com.jobhelper.careerflowapi.user.domain.enums.Provider;
import com.jobhelper.careerflowapi.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountCommandHandler {

    private final UserRepository userRepository;
    private final EmailAccountStrategy emailAccountStrategy;
    private final SocialAccountStrategy socialAccountStrategy;
    private final VerificationService verificationService;
    private final UserCreationService userCreationService;

    public void register(LocalSignupRequest request) {
        if (userRepository.existsByNickname(request.nickname())) {
            throw new BusinessException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }
        emailAccountStrategy.register(request);
    }

    public LoginResult login(LocalLoginRequest request) {
        return emailAccountStrategy.authenticate(request);
    }

    public LoginResult oAuth2Login(Provider provider, String code) {
        return socialAccountStrategy.authenticate(provider, code);
    }

    public String getOAuth2AuthorizationUri(Provider provider) {
        return socialAccountStrategy.getAuthorizationUri(provider);
    }

    public void verifyEmail(String email, String code) {
        PendingSignup pending = verificationService.verify(email, code);
        userCreationService.createUser(pending);
    }

    public void resendVerification(String email) {
        verificationService.resend(email);
    }
}