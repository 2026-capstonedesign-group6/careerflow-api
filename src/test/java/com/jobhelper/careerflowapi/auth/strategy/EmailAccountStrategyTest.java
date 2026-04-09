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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailAccountStrategyTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private PendingSignupRepository pendingSignupRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private VerificationService verificationService;

    @Mock
    private AuthSessionService authSessionService;

    @InjectMocks
    private EmailAccountStrategy emailAccountStrategy;

    // ==================== register ====================

    @Test
    void register_이메일_중복시_EMAIL_ALREADY_EXISTS_예외를_던진다() {
        LocalSignupRequest request = new LocalSignupRequest("dup@test.com", "닉네임", "password123!");
        given(userRepository.existsByEmail("dup@test.com")).willReturn(true);

        assertThatThrownBy(() -> emailAccountStrategy.register(request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.EMAIL_ALREADY_EXISTS);
    }

    @Test
    void register_신규_요청이면_PendingSignup을_저장하고_인증_메일을_발송한다() {
        LocalSignupRequest request = new LocalSignupRequest("new@test.com", "닉네임", "password123!");
        given(userRepository.existsByEmail("new@test.com")).willReturn(false);
        given(pendingSignupRepository.findByEmail("new@test.com")).willReturn(Optional.empty());
        given(passwordEncoder.encode("password123!")).willReturn("encoded-pw");

        emailAccountStrategy.register(request);

        verify(pendingSignupRepository).save(any(PendingSignup.class));
        verify(verificationService).sendVerificationEmail("new@test.com", "닉네임");
    }

    @Test
    void register_기존_PendingSignup이_있으면_갱신하고_인증_메일을_재발송한다() {
        LocalSignupRequest request = new LocalSignupRequest("existing@test.com", "닉네임", "password123!");
        PendingSignup existing = PendingSignup.of("existing@test.com", "old-nick", "old-pw");
        given(userRepository.existsByEmail("existing@test.com")).willReturn(false);
        given(pendingSignupRepository.findByEmail("existing@test.com")).willReturn(Optional.of(existing));
        given(passwordEncoder.encode("password123!")).willReturn("encoded-pw");

        emailAccountStrategy.register(request);

        verify(pendingSignupRepository).save(any(PendingSignup.class));
        verify(verificationService).sendVerificationEmail("existing@test.com", "닉네임");
    }

    @Test
    void register_시도_횟수_초과시_TOO_MANY_REQUESTS_예외를_던진다() {
        LocalSignupRequest request = new LocalSignupRequest("blocked@test.com", "닉네임", "password123!");
        PendingSignup blocked = new PendingSignup("blocked@test.com", "닉네임", "pw", 6);
        given(userRepository.existsByEmail("blocked@test.com")).willReturn(false);
        given(pendingSignupRepository.findByEmail("blocked@test.com")).willReturn(Optional.of(blocked));
        given(passwordEncoder.encode(any())).willReturn("encoded-pw");

        assertThatThrownBy(() -> emailAccountStrategy.register(request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.TOO_MANY_REQUESTS);
    }

    // ==================== authenticate ====================

    @Test
    void authenticate_올바른_자격증명으로_로그인_결과를_반환한다() {
        LocalLoginRequest request = new LocalLoginRequest("user@test.com", "password123!");
        User user = User.builder().email("user@test.com").nickname("테스터").role("ROLE_USER").build();
        UserAccount account = UserAccount.builder().provider(Provider.LOCAL).password("encoded-pw").build();
        user.addAccount(account);
        LoginResult loginResult = new LoginResult("access-token", ResponseCookie.from("refreshToken", "rt").build());

        given(userRepository.findByEmail("user@test.com")).willReturn(Optional.of(user));
        given(userAccountRepository.findByUserAndProvider(user, Provider.LOCAL)).willReturn(Optional.of(account));
        given(passwordEncoder.matches("password123!", "encoded-pw")).willReturn(true);
        given(authSessionService.createSession(user)).willReturn(loginResult);

        LoginResult result = emailAccountStrategy.authenticate(request);

        assertThat(result).isSameAs(loginResult);
    }

    @Test
    void authenticate_존재하지_않는_이메일이면_USER_NOT_FOUND_예외를_던지고_이벤트를_발행한다() {
        LocalLoginRequest request = new LocalLoginRequest("notfound@test.com", "password123!");
        given(userRepository.findByEmail("notfound@test.com")).willReturn(Optional.empty());

        assertThatThrownBy(() -> emailAccountStrategy.authenticate(request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND);

        verify(eventPublisher).publishEvent(any(LoginFailedEvent.class));
    }

    @Test
    void authenticate_로컬_계정이_없으면_USER_NOT_FOUND_예외를_던진다() {
        LocalLoginRequest request = new LocalLoginRequest("user@test.com", "password123!");
        User user = User.builder().email("user@test.com").nickname("테스터").role("ROLE_USER").build();

        given(userRepository.findByEmail("user@test.com")).willReturn(Optional.of(user));
        given(userAccountRepository.findByUserAndProvider(user, Provider.LOCAL)).willReturn(Optional.empty());

        assertThatThrownBy(() -> emailAccountStrategy.authenticate(request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND);

        verify(eventPublisher).publishEvent(any(LoginFailedEvent.class));
    }

    @Test
    void authenticate_비밀번호_불일치시_PASSWORD_NOT_MATCH_예외를_던진다() {
        LocalLoginRequest request = new LocalLoginRequest("user@test.com", "wrong-pw");
        User user = User.builder().email("user@test.com").nickname("테스터").role("ROLE_USER").build();
        UserAccount account = UserAccount.builder().provider(Provider.LOCAL).password("encoded-pw").build();
        user.addAccount(account);

        given(userRepository.findByEmail("user@test.com")).willReturn(Optional.of(user));
        given(userAccountRepository.findByUserAndProvider(user, Provider.LOCAL)).willReturn(Optional.of(account));
        given(passwordEncoder.matches("wrong-pw", "encoded-pw")).willReturn(false);

        assertThatThrownBy(() -> emailAccountStrategy.authenticate(request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.PASSWORD_NOT_MATCH);

        verify(eventPublisher).publishEvent(any(LoginFailedEvent.class));
        verify(authSessionService, never()).createSession(any());
    }
}
