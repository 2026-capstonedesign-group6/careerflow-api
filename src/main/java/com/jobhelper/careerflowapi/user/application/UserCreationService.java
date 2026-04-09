package com.jobhelper.careerflowapi.user.application;

import com.jobhelper.careerflowapi.user.domain.entity.User;
import com.jobhelper.careerflowapi.user.domain.entity.UserAccount;
import com.jobhelper.careerflowapi.user.domain.enums.Provider;
import com.jobhelper.careerflowapi.user.domain.verification.PendingSignup;
import com.jobhelper.careerflowapi.user.event.UserRegisterEvent;
import com.jobhelper.careerflowapi.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserCreationService {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void createUser(PendingSignup pending) {
        User user = User.builder()
                .email(pending.getEmail())
                .nickname(pending.getNickname())
                .role("ROLE_USER")
                .build();

        UserAccount account = UserAccount.builder()
                .provider(Provider.LOCAL)
                .password(pending.getEncodedPassword())
                .build();

        user.addAccount(account);
        userRepository.save(user);

        eventPublisher.publishEvent(new UserRegisterEvent(user));
    }
}
