package com.jobhelper.careerflowapi.global.security.jwt;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class TokenCookieFactoryTest {

    private static final String TEST_SECRET = Base64.getUrlEncoder()
            .encodeToString("test-secret-key-for-cookie-factory".getBytes());
    private static final long REFRESH_TTL = 1209600L;

    private final JwtProperties props = new JwtProperties(TEST_SECRET, 1800L, REFRESH_TTL, true);
    private final TokenCookieFactory factory = new TokenCookieFactory(props);

    @Test
    void createRefreshTokenCookie_httpOnly_secure_쿠키를_생성한다() {
        ResponseCookie cookie = factory.createRefreshTokenCookie("refresh-token-value");

        assertThat(cookie.getName()).isEqualTo("refreshToken");
        assertThat(cookie.getValue()).isEqualTo("refresh-token-value");
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.isSecure()).isTrue();
        assertThat(cookie.getPath()).isEqualTo("/api/auth/refresh");
        assertThat(cookie.getMaxAge().getSeconds()).isEqualTo(REFRESH_TTL);
        assertThat(cookie.getSameSite()).isEqualTo("None");
    }

    @Test
    void createRefreshTokenCookie_cookieSecure_false이면_secure_false로_생성한다() {
        JwtProperties insecureProps = new JwtProperties(TEST_SECRET, 1800L, REFRESH_TTL, false);
        TokenCookieFactory insecureFactory = new TokenCookieFactory(insecureProps);

        ResponseCookie cookie = insecureFactory.createRefreshTokenCookie("refresh-token");

        assertThat(cookie.isSecure()).isFalse();
    }

    @Test
    void deleteRefreshTokenCookie_maxAge_0인_쿠키를_생성한다() {
        ResponseCookie cookie = factory.deleteRefreshTokenCookie();

        assertThat(cookie.getName()).isEqualTo("refreshToken");
        assertThat(cookie.getValue()).isEmpty();
        assertThat(cookie.getMaxAge().getSeconds()).isZero();
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.getPath()).isEqualTo("/api/auth/refresh");
    }
}
