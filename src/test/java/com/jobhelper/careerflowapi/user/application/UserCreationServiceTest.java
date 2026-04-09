package com.jobhelper.careerflowapi.user.application;

import com.jobhelper.careerflowapi.auth.domain.verification.PendingSignup;
import com.jobhelper.careerflowapi.user.domain.entity.User;
import com.jobhelper.careerflowapi.user.event.UserRegisterEvent;
import com.jobhelper.careerflowapi.user.infrastructure.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserCreationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private UserCreationService userCreationService;

    @Test
    void createUser_PendingSignup으로_User와_UserAccount를_생성하고_저장한다() {
        PendingSignup pending = PendingSignup.of("user@test.com", "테스터", "encoded-pw");
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        given(userRepository.save(any(User.class))).willAnswer(i -> i.getArgument(0));

        userCreationService.createUser(pending);

        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getEmail()).isEqualTo("user@test.com");
        assertThat(savedUser.getNickname()).isEqualTo("테스터");
        assertThat(savedUser.getRole()).isEqualTo("ROLE_USER");
        assertThat(savedUser.getAccounts()).hasSize(1);
        assertThat(savedUser.getAccounts().get(0).getPassword()).isEqualTo("encoded-pw");
    }

    @Test
    void createUser_UserRegisterEvent를_발행한다() {
        PendingSignup pending = PendingSignup.of("user@test.com", "테스터", "encoded-pw");
        given(userRepository.save(any(User.class))).willAnswer(i -> i.getArgument(0));

        userCreationService.createUser(pending);

        ArgumentCaptor<UserRegisterEvent> eventCaptor = ArgumentCaptor.forClass(UserRegisterEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue().user().getEmail()).isEqualTo("user@test.com");
    }
}
