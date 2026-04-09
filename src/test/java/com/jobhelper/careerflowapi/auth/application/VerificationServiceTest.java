package com.jobhelper.careerflowapi.auth.application;

import com.jobhelper.careerflowapi.auth.domain.verification.PendingSignup;
import com.jobhelper.careerflowapi.auth.domain.verification.VerificationSession;
import com.jobhelper.careerflowapi.auth.infrastructure.PendingSignupRepository;
import com.jobhelper.careerflowapi.auth.infrastructure.VerificationSessionRepository;
import com.jobhelper.careerflowapi.global.domain.ErrorCode;
import com.jobhelper.careerflowapi.global.exception.BusinessException;
import com.jobhelper.careerflowapi.user.infrastructure.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class VerificationServiceTest {

    @Mock
    private VerificationSessionRepository verificationSessionRepository;

    @Mock
    private PendingSignupRepository pendingSignupRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MailService mailService;

    @InjectMocks
    private VerificationService verificationService;

    @Test
    void sendVerificationEmail_인증_세션을_저장하고_메일을_전송한다() {
        verificationService.sendVerificationEmail("user@test.com", "테스터");

        verify(verificationSessionRepository).save(any(VerificationSession.class));
        verify(mailService).sendVerificationCode(any(), any(), any());
    }

    @Test
    void verify_올바른_코드로_PendingSignup을_반환한다() {
        VerificationSession session = VerificationSession.of("user@test.com", "123456");
        PendingSignup pending = PendingSignup.of("user@test.com", "테스터", "encoded-pw");

        given(verificationSessionRepository.findByEmail("user@test.com")).willReturn(Optional.of(session));
        given(userRepository.existsByEmail("user@test.com")).willReturn(false);
        given(pendingSignupRepository.findByEmail("user@test.com")).willReturn(Optional.of(pending));

        PendingSignup result = verificationService.verify("user@test.com", "123456");

        assertThat(result).isSameAs(pending);
        verify(verificationSessionRepository).delete("user@test.com");
        verify(pendingSignupRepository).delete("user@test.com");
    }

    @Test
    void verify_세션이_없으면_VERIFICATION_EXPIRED_예외를_던진다() {
        given(verificationSessionRepository.findByEmail("user@test.com")).willReturn(Optional.empty());

        assertThatThrownBy(() -> verificationService.verify("user@test.com", "123456"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.VERIFICATION_EXPIRED);
    }

    @Test
    void verify_코드_불일치시_VERIFICATION_CODE_MISMATCH_예외를_던진다() {
        VerificationSession session = VerificationSession.of("user@test.com", "123456");

        given(verificationSessionRepository.findByEmail("user@test.com")).willReturn(Optional.of(session));

        assertThatThrownBy(() -> verificationService.verify("user@test.com", "000000"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.VERIFICATION_CODE_MISMATCH);
    }

    @Test
    void verify_이미_가입된_사용자면_VERIFICATION_ALREADY_COMPLETED_예외를_던진다() {
        VerificationSession session = VerificationSession.of("user@test.com", "123456");

        given(verificationSessionRepository.findByEmail("user@test.com")).willReturn(Optional.of(session));
        given(userRepository.existsByEmail("user@test.com")).willReturn(true);

        assertThatThrownBy(() -> verificationService.verify("user@test.com", "123456"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.VERIFICATION_ALREADY_COMPLETED);
    }

    @Test
    void verify_PendingSignup이_없으면_VERIFICATION_EXPIRED_예외를_던진다() {
        VerificationSession session = VerificationSession.of("user@test.com", "123456");

        given(verificationSessionRepository.findByEmail("user@test.com")).willReturn(Optional.of(session));
        given(userRepository.existsByEmail("user@test.com")).willReturn(false);
        given(pendingSignupRepository.findByEmail("user@test.com")).willReturn(Optional.empty());

        assertThatThrownBy(() -> verificationService.verify("user@test.com", "123456"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.VERIFICATION_EXPIRED);
    }

    @Test
    void resend_PendingSignup이_존재하면_이메일을_재전송한다() {
        PendingSignup pending = PendingSignup.of("user@test.com", "테스터", "encoded-pw");

        given(userRepository.existsByEmail("user@test.com")).willReturn(false);
        given(pendingSignupRepository.findByEmail("user@test.com")).willReturn(Optional.of(pending));

        verificationService.resend("user@test.com");

        verify(verificationSessionRepository).save(any(VerificationSession.class));
        verify(mailService).sendVerificationCode(any(), any(), any());
    }

    @Test
    void resend_이미_가입된_사용자면_VERIFICATION_ALREADY_COMPLETED_예외를_던진다() {
        given(userRepository.existsByEmail("user@test.com")).willReturn(true);

        assertThatThrownBy(() -> verificationService.resend("user@test.com"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.VERIFICATION_ALREADY_COMPLETED);
    }

    @Test
    void resend_PendingSignup이_없으면_VERIFICATION_NOT_FOUND_예외를_던진다() {
        given(userRepository.existsByEmail("user@test.com")).willReturn(false);
        given(pendingSignupRepository.findByEmail("user@test.com")).willReturn(Optional.empty());

        assertThatThrownBy(() -> verificationService.resend("user@test.com"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.VERIFICATION_NOT_FOUND);
    }
}
