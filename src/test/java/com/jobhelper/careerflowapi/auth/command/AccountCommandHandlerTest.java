package com.jobhelper.careerflowapi.auth.command;

import com.jobhelper.careerflowapi.auth.application.VerificationService;
import com.jobhelper.careerflowapi.auth.domain.verification.PendingSignup;
import com.jobhelper.careerflowapi.auth.presentation.dto.request.LocalLoginRequest;
import com.jobhelper.careerflowapi.auth.presentation.dto.request.LocalSignupRequest;
import com.jobhelper.careerflowapi.auth.strategy.EmailAccountStrategy;
import com.jobhelper.careerflowapi.auth.strategy.SocialAccountStrategy;
import com.jobhelper.careerflowapi.global.domain.ErrorCode;
import com.jobhelper.careerflowapi.global.exception.BusinessException;
import com.jobhelper.careerflowapi.user.application.UserCreationService;
import com.jobhelper.careerflowapi.user.infrastructure.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountCommandHandlerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailAccountStrategy emailAccountStrategy;

    @Mock
    private SocialAccountStrategy socialAccountStrategy;

    @Mock
    private VerificationService verificationService;

    @Mock
    private UserCreationService userCreationService;

    @InjectMocks
    private AccountCommandHandler accountCommandHandler;

    @Test
    void register_닉네임_중복시_NICKNAME_ALREADY_EXISTS_예외를_던진다() {
        LocalSignupRequest request = new LocalSignupRequest("user@test.com", "중복닉네임", "password123!");
        given(userRepository.existsByNickname("중복닉네임")).willReturn(true);

        assertThatThrownBy(() -> accountCommandHandler.register(request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.NICKNAME_ALREADY_EXISTS);
    }

    @Test
    void register_닉네임_중복이_없으면_전략에_위임한다() {
        LocalSignupRequest request = new LocalSignupRequest("user@test.com", "닉네임", "password123!");
        given(userRepository.existsByNickname("닉네임")).willReturn(false);

        accountCommandHandler.register(request);

        verify(emailAccountStrategy).register(request);
    }

    @Test
    void login_전략에_인증을_위임한다() {
        LocalLoginRequest request = new LocalLoginRequest("user@test.com", "password123!");

        accountCommandHandler.login(request);

        verify(emailAccountStrategy).authenticate(request);
    }

    @Test
    void verifyEmail_검증_후_사용자를_생성한다() {
        PendingSignup pending = PendingSignup.of("user@test.com", "테스터", "encoded-pw");
        given(verificationService.verify("user@test.com", "123456")).willReturn(pending);

        accountCommandHandler.verifyEmail("user@test.com", "123456");

        verify(verificationService).verify("user@test.com", "123456");
        verify(userCreationService).createUser(pending);
    }

    @Test
    void resendVerification_재발송을_서비스에_위임한다() {
        accountCommandHandler.resendVerification("user@test.com");

        verify(verificationService).resend("user@test.com");
    }
}
