package com.jobhelper.careerflowapi.global.security.jwt.impls;

import com.jobhelper.careerflowapi.global.domain.ErrorCode;
import com.jobhelper.careerflowapi.global.exception.AuthorizationException;
import com.jobhelper.careerflowapi.global.security.jwt.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtTokenValidatorImplTest {

    private static final String TEST_SECRET = Base64.getUrlEncoder()
            .encodeToString("test-secret-key-for-jwt-validator-unit-test".getBytes());

    private JwtTokenValidatorImpl validator;
    private JwtTokenProviderImpl provider;

    @BeforeEach
    void setUp() {
        JwtProperties props = new JwtProperties(TEST_SECRET, 1800L, 1209600L, false);
        provider = new JwtTokenProviderImpl(props);
        provider.init();

        validator = new JwtTokenValidatorImpl(props);
        validator.init();
    }

    // ==================== validateToken ====================

    @Test
    void validateToken_유효한_토큰은_예외없이_통과한다() {
        String token = provider.createAccessToken(1L, "user@test.com",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        validator.validateToken(token);
    }

    @Test
    void validateToken_만료된_토큰은_JWT_EXPIRED_예외를_던진다() {
        JwtProperties expiredProps = new JwtProperties(TEST_SECRET, -1L, -1L, false);
        JwtTokenProviderImpl expiredProvider = new JwtTokenProviderImpl(expiredProps);
        expiredProvider.init();
        String token = expiredProvider.createAccessToken(1L, "user@test.com",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        assertThatThrownBy(() -> validator.validateToken(token))
                .isInstanceOf(AuthorizationException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.JWT_EXPIRED);
    }

    @Test
    void validateToken_변조된_토큰은_JWT_MALFORMED_예외를_던진다() {
        String malformedToken = "malformed.jwt.token";

        assertThatThrownBy(() -> validator.validateToken(malformedToken))
                .isInstanceOf(AuthorizationException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.JWT_MALFORMED);
    }

    @Test
    void validateToken_빈_문자열은_JWT_MISSING_예외를_던진다() {
        assertThatThrownBy(() -> validator.validateToken(""))
                .isInstanceOf(AuthorizationException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.JWT_MISSING);
    }

    // ==================== resolveToken ====================

    @Test
    void resolveToken_Bearer_토큰을_추출한다() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer test-token-value");

        Optional<String> result = validator.resolveToken(request);

        assertThat(result).isPresent().contains("test-token-value");
    }

    @Test
    void resolveToken_Authorization_헤더가_없으면_빈_Optional을_반환한다() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        Optional<String> result = validator.resolveToken(request);

        assertThat(result).isEmpty();
    }

    @Test
    void resolveToken_Bearer_prefix가_없으면_빈_Optional을_반환한다() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Basic user:pass");

        Optional<String> result = validator.resolveToken(request);

        assertThat(result).isEmpty();
    }

    // ==================== getAuthentication ====================

    @Test
    void getAuthentication_유효한_액세스_토큰에서_Authentication을_생성한다() {
        String token = provider.createAccessToken(1L, "user@test.com",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        Authentication authentication = validator.getAuthentication(token);

        assertThat(authentication.getName()).isEqualTo("user@test.com");
        assertThat(authentication.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_USER");
    }

    // ==================== getEmailFromRefreshToken ====================

    @Test
    void getEmailFromRefreshToken_리프레시_토큰에서_이메일을_추출한다() {
        String refreshToken = provider.createRefreshToken("user@test.com");

        Optional<String> email = validator.getEmailFromRefreshToken(refreshToken);

        assertThat(email).isPresent().contains("user@test.com");
    }
}
