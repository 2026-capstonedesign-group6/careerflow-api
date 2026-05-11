package com.jobhelper.careerflowapi.global.security;

import com.jobhelper.careerflowapi.global.domain.ErrorCode;
import com.jobhelper.careerflowapi.global.exception.AuthorizationException;
import com.jobhelper.careerflowapi.global.security.dto.AuthTokens;
import com.jobhelper.careerflowapi.global.security.dto.AuthUser;
import com.jobhelper.careerflowapi.global.security.jwt.JwtProperties;
import com.jobhelper.careerflowapi.global.security.jwt.JwtTokenProvider;
import com.jobhelper.careerflowapi.global.security.jwt.JwtTokenValidator;
import com.jobhelper.careerflowapi.global.security.jwt.TokenCookieFactory;
import com.jobhelper.careerflowapi.global.security.principal.PrincipalDetails;
import com.jobhelper.careerflowapi.global.security.token.JwtAuthenticationToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SecurityFacadeImplTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private JwtTokenValidator jwtTokenValidator;

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private TokenCookieFactory tokenCookieFactory;

    @InjectMocks
    private SecurityFacadeImpl securityFacade;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void issueTokens_액세스_토큰과_리프레시_토큰을_발급한다() {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "rt").build();
        given(jwtTokenProvider.createAccessToken(eq(1L), eq("user@test.com"), any())).willReturn("access-token");
        given(jwtTokenProvider.createRefreshToken("user@test.com")).willReturn("refresh-token");
        given(jwtProperties.refreshTokenValidityInSeconds()).willReturn(1209600L);
        given(tokenCookieFactory.createRefreshTokenCookie("refresh-token")).willReturn(cookie);

        AuthTokens tokens = securityFacade.issueTokens(1L, "user@test.com", "ROLE_USER");

        assertThat(tokens.accessToken()).isEqualTo("access-token");
        assertThat(tokens.refreshToken()).isEqualTo("refresh-token");
        assertThat(tokens.refreshTokenTtlSeconds()).isEqualTo(1209600L);
        assertThat(tokens.refreshCookie()).isSameAs(cookie);
    }

    @Test
    void validateToken_검증을_위임한다() {
        securityFacade.validateToken("some-token");

        verify(jwtTokenValidator).validateToken("some-token");
    }

    @Test
    void getEmailFromToken_이메일_추출을_위임한다() {
        given(jwtTokenValidator.getEmailFromRefreshToken("refresh-token"))
                .willReturn(Optional.of("user@test.com"));

        Optional<String> email = securityFacade.getEmailFromToken("refresh-token");

        assertThat(email).isPresent().contains("user@test.com");
    }

    @Test
    void deleteRefreshTokenCookie_삭제_쿠키_생성을_위임한다() {
        ResponseCookie emptyCookie = ResponseCookie.from("refreshToken", "").maxAge(0).build();
        given(tokenCookieFactory.deleteRefreshTokenCookie()).willReturn(emptyCookie);

        ResponseCookie result = securityFacade.deleteRefreshTokenCookie();

        assertThat(result).isSameAs(emptyCookie);
    }

    @Test
    void getCurrentUser_SecurityContext에서_인증된_사용자를_반환한다() {
        AuthUser authUser = AuthUser.of(1L, "user@test.com", "ROLE_USER");
        PrincipalDetails principal = PrincipalDetails.from(authUser);
        JwtAuthenticationToken auth = new JwtAuthenticationToken(principal, "token",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        AuthUser result = securityFacade.getCurrentUser();

        assertThat(result.userId()).isEqualTo(1L);
        assertThat(result.email()).isEqualTo("user@test.com");
        assertThat(result.role()).isEqualTo("ROLE_USER");
    }

    @Test
    void getCurrentUser_인증_정보가_없으면_UNAUTHORIZED_예외를_던진다() {
        SecurityContextHolder.clearContext();

        assertThatThrownBy(() -> securityFacade.getCurrentUser())
                .isInstanceOf(AuthorizationException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.UNAUTHORIZED);
    }
}
