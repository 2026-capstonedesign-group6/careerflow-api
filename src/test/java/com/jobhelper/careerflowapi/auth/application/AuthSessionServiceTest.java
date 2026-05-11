package com.jobhelper.careerflowapi.auth.application;

import com.jobhelper.careerflowapi.auth.application.dto.LoginResult;
import com.jobhelper.careerflowapi.auth.domain.session.AuthSession;
import com.jobhelper.careerflowapi.auth.infrastructure.AuthSessionRepository;
import com.jobhelper.careerflowapi.global.domain.ErrorCode;
import com.jobhelper.careerflowapi.global.exception.AuthorizationException;
import com.jobhelper.careerflowapi.global.exception.BusinessException;
import com.jobhelper.careerflowapi.global.security.SecurityFacade;
import com.jobhelper.careerflowapi.global.security.dto.AuthTokens;
import com.jobhelper.careerflowapi.user.domain.entity.User;
import com.jobhelper.careerflowapi.user.infrastructure.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class AuthSessionServiceTest {

    @Mock
    private AuthSessionRepository authSessionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityFacade securityFacade;

    @InjectMocks
    private AuthSessionService authSessionService;

    @Test
    void createSession_토큰을_발급하고_세션을_저장한다() {
        User user = User.builder().email("user@test.com").nickname("테스터").role("ROLE_USER").build();
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "rt").build();
        AuthTokens tokens = new AuthTokens("access-token", "refresh-token", 1209600L, cookie);

        given(securityFacade.issueTokens(any(), eq("user@test.com"), eq("ROLE_USER"))).willReturn(tokens);

        LoginResult result = authSessionService.createSession(user);

        assertThat(result.accessToken()).isEqualTo("access-token");
        assertThat(result.refreshCookie()).isSameAs(cookie);
        verify(authSessionRepository).save(any(AuthSession.class), eq(1209600L));
    }

    @Test
    void refresh_유효한_토큰으로_세션을_갱신한다() {
        String email = "user@test.com";
        String oldRefreshToken = "old-refresh-token";
        AuthSession session = AuthSession.of(email, oldRefreshToken);
        User user = User.builder().email(email).nickname("테스터").role("ROLE_USER").build();
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "new-rt").build();
        AuthTokens tokens = new AuthTokens("new-access-token", "new-refresh-token", 1209600L, cookie);

        given(securityFacade.getEmailFromToken(oldRefreshToken)).willReturn(Optional.of(email));
        given(authSessionRepository.findByEmail(email)).willReturn(Optional.of(session));
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(securityFacade.issueTokens(any(), eq(email), eq("ROLE_USER"))).willReturn(tokens);

        LoginResult result = authSessionService.refresh(oldRefreshToken);

        assertThat(result.accessToken()).isEqualTo("new-access-token");
        verify(authSessionRepository).save(any(AuthSession.class), anyLong());
    }

    @Test
    void refresh_이메일_추출_실패시_예외를_던진다() {
        given(securityFacade.getEmailFromToken("bad-token")).willReturn(Optional.empty());

        assertThatThrownBy(() -> authSessionService.refresh("bad-token"))
                .isInstanceOf(AuthorizationException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_REFRESH_TOKEN);
    }

    @Test
    void refresh_세션이_없으면_예외를_던진다() {
        given(securityFacade.getEmailFromToken("refresh-token")).willReturn(Optional.of("user@test.com"));
        given(authSessionRepository.findByEmail("user@test.com")).willReturn(Optional.empty());

        assertThatThrownBy(() -> authSessionService.refresh("refresh-token"))
                .isInstanceOf(AuthorizationException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
    }

    @Test
    void refresh_토큰_탈취_감지시_세션을_삭제하고_예외를_던진다() {
        String email = "user@test.com";
        AuthSession session = AuthSession.of(email, "stored-token");

        given(securityFacade.getEmailFromToken("stolen-token")).willReturn(Optional.of(email));
        given(authSessionRepository.findByEmail(email)).willReturn(Optional.of(session));

        assertThatThrownBy(() -> authSessionService.refresh("stolen-token"))
                .isInstanceOf(AuthorizationException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.REFRESH_TOKEN_THEFT_DETECTED);

        verify(authSessionRepository).delete(email);
    }

    @Test
    void refresh_사용자가_없으면_예외를_던진다() {
        String email = "user@test.com";
        String token = "valid-token";
        AuthSession session = AuthSession.of(email, token);

        given(securityFacade.getEmailFromToken(token)).willReturn(Optional.of(email));
        given(authSessionRepository.findByEmail(email)).willReturn(Optional.of(session));
        given(userRepository.findByEmail(email)).willReturn(Optional.empty());

        assertThatThrownBy(() -> authSessionService.refresh(token))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    void logout_이메일_추출_성공시_세션을_삭제한다() {
        String email = "user@test.com";
        ResponseCookie emptyCookie = ResponseCookie.from("refreshToken", "").maxAge(0).build();

        given(securityFacade.getEmailFromToken("refresh-token")).willReturn(Optional.of(email));
        given(securityFacade.deleteRefreshTokenCookie()).willReturn(emptyCookie);

        ResponseCookie result = authSessionService.logout("refresh-token");

        assertThat(result).isSameAs(emptyCookie);
        verify(authSessionRepository).delete(email);
    }

    @Test
    void logout_이메일_추출_실패시_세션_삭제없이_쿠키만_반환한다() {
        ResponseCookie emptyCookie = ResponseCookie.from("refreshToken", "").maxAge(0).build();

        given(securityFacade.getEmailFromToken("bad-token")).willReturn(Optional.empty());
        given(securityFacade.deleteRefreshTokenCookie()).willReturn(emptyCookie);

        ResponseCookie result = authSessionService.logout("bad-token");

        assertThat(result).isSameAs(emptyCookie);
        verifyNoInteractions(authSessionRepository);
    }
}
