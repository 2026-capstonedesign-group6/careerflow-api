package com.jobhelper.careerflowapi.global.security.jwt.impls;

import com.jobhelper.careerflowapi.global.security.jwt.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderImplTest {

    private static final String TEST_SECRET = Base64.getUrlEncoder()
            .encodeToString("test-secret-key-for-jwt-provider-unit-test".getBytes());
    private static final long ACCESS_TOKEN_VALIDITY = 1800L;
    private static final long REFRESH_TOKEN_VALIDITY = 1209600L;

    private JwtTokenProviderImpl provider;
    private SecretKey verifyKey;

    @BeforeEach
    void setUp() {
        JwtProperties props = new JwtProperties(TEST_SECRET, ACCESS_TOKEN_VALIDITY, REFRESH_TOKEN_VALIDITY, false);
        provider = new JwtTokenProviderImpl(props);
        provider.init();
        verifyKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(TEST_SECRET));
    }

    @Test
    void createAccessToken_이메일과_권한을_클레임에_포함한_토큰을_생성한다() {
        String token = provider.createAccessToken(1L, "user@test.com",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        Claims claims = parseClaims(token);
        assertThat(claims.getSubject()).isEqualTo("user@test.com");
        assertThat(claims.get("uid", Long.class)).isEqualTo(1L);
        assertThat(claims.get("auth", String.class)).isEqualTo("ROLE_USER");
    }

    @Test
    void createAccessToken_만료_시간이_설정된다() {
        String token = provider.createAccessToken(1L, "user@test.com",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        Claims claims = parseClaims(token);
        long ttl = (claims.getExpiration().getTime() - claims.getIssuedAt().getTime()) / 1000;
        assertThat(ttl).isEqualTo(ACCESS_TOKEN_VALIDITY);
    }

    @Test
    void createRefreshToken_이메일을_subject로_설정한다() {
        String token = provider.createRefreshToken("user@test.com");

        Claims claims = parseClaims(token);
        assertThat(claims.getSubject()).isEqualTo("user@test.com");
    }

    @Test
    void createRefreshToken_만료_시간이_설정된다() {
        String token = provider.createRefreshToken("user@test.com");

        Claims claims = parseClaims(token);
        long ttl = (claims.getExpiration().getTime() - claims.getIssuedAt().getTime()) / 1000;
        assertThat(ttl).isEqualTo(REFRESH_TOKEN_VALIDITY);
    }

    @Test
    void createAccessToken_여러_권한을_콤마로_구분하여_저장한다() {
        String token = provider.createAccessToken(1L, "user@test.com",
                List.of(new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("ROLE_ADMIN")));

        Claims claims = parseClaims(token);
        assertThat(claims.get("auth", String.class)).contains("ROLE_USER").contains("ROLE_ADMIN");
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(verifyKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
