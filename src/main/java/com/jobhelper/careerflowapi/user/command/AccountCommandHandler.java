package com.jobhelper.careerflowapi.user.command;

import com.jobhelper.careerflowapi.global.domain.ErrorCode;
import com.jobhelper.careerflowapi.global.exception.BusinessException;
import com.jobhelper.careerflowapi.user.application.LoginResult;
import com.jobhelper.careerflowapi.user.domain.enums.Provider;
import com.jobhelper.careerflowapi.user.infrastructure.UserRepository;
import com.jobhelper.careerflowapi.user.presentation.dto.request.LocalLoginRequest;
import com.jobhelper.careerflowapi.user.presentation.dto.request.LocalSignupRequest;
import com.jobhelper.careerflowapi.user.strategy.EmailAccountStrategy;
import com.jobhelper.careerflowapi.user.strategy.SocialAccountStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountCommandHandler {

    private final UserRepository userRepository;
    private final EmailAccountStrategy emailAccountStrategy;
    private final SocialAccountStrategy socialAccountStrategy;

    @Transactional
    public void register(LocalSignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
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
}
