package com.jobhelper.careerflowapi.user.strategy;

import com.jobhelper.careerflowapi.global.domain.ErrorCode;
import com.jobhelper.careerflowapi.global.exception.BusinessException;
import com.jobhelper.careerflowapi.user.application.LoginResult;
import com.jobhelper.careerflowapi.user.domain.entity.User;
import com.jobhelper.careerflowapi.user.domain.entity.UserAccount;
import com.jobhelper.careerflowapi.user.domain.enums.Provider;
import com.jobhelper.careerflowapi.user.event.LoginEvent;
import com.jobhelper.careerflowapi.user.event.UserRegisterEvent;
import com.jobhelper.careerflowapi.user.infrastructure.UserAccountRepository;
import com.jobhelper.careerflowapi.user.infrastructure.UserRepository;
import com.jobhelper.careerflowapi.user.oauth2.OAuthClient;
import com.jobhelper.careerflowapi.user.oauth2.OAuthUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SocialAccountStrategy implements AccountStrategy {

    private final List<OAuthClient> oAuthClients;
    private final UserRepository userRepository;
    private final UserAccountRepository userAccountRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public LoginResult authenticate(Provider provider, String code) {
        OAuthClient client = oAuthClients.stream()
                .filter(c -> c.provider() == provider)
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_PROVIDER));

        OAuthUserInfo userInfo = client.getUserInfo(code);

        Optional<UserAccount> existingAccount =
                userAccountRepository.findByProviderAndProviderId(provider, userInfo.providerId());

        User user;
        if (existingAccount.isPresent()) {
            user = existingAccount.get().getUser();
        } else {
            if (userRepository.existsByEmail(userInfo.email())) {
                throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
            }
            user = registerSocialUser(provider, userInfo);
        }

        LoginEvent event = new LoginEvent(user);
        eventPublisher.publishEvent(event);
        return event.getResult();
    }

    public String getAuthorizationUri(Provider provider) {
        return oAuthClients.stream()
                .filter(c -> c.provider() == provider)
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_PROVIDER))
                .getAuthorizationUri();
    }

    private User registerSocialUser(Provider provider, OAuthUserInfo userInfo) {
        String nickname = resolveUniqueNickname(userInfo.nickname());

        User user = User.builder()
                .email(userInfo.email())
                .nickname(nickname)
                .role("ROLE_USER")
                .build();

        UserAccount account = UserAccount.builder()
                .provider(provider)
                .providerId(userInfo.providerId())
                .build();

        user.addAccount(account);
        userRepository.save(user);

        eventPublisher.publishEvent(new UserRegisterEvent(user));
        return user;
    }

    private String resolveUniqueNickname(String candidate) {
        if (candidate != null && !userRepository.existsByNickname(candidate)) {
            return candidate;
        }
        String base = (candidate != null) ? candidate : "user";
        String nickname;
        do {
            nickname = base + "_" + UUID.randomUUID().toString().substring(0, 6);
        } while (userRepository.existsByNickname(nickname));
        return nickname;
    }
}
