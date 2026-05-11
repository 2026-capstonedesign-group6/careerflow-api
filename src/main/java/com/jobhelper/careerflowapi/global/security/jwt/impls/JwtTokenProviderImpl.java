package com.jobhelper.careerflowapi.global.security.jwt.impls;

import com.jobhelper.careerflowapi.global.security.jwt.JwtProperties;
import com.jobhelper.careerflowapi.global.security.jwt.JwtTokenProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProviderImpl implements JwtTokenProvider {

    private static final String AUTHORITIES_KEY = "auth";
    private static final String USER_ID_KEY = "uid";

    private final JwtProperties jwtProperties;
    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64URL.decode(jwtProperties.secret());
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String createAccessToken(Long userId, String email, Collection<? extends GrantedAuthority> authorities) {
        Instant now = Instant.now();
        Instant accessTokenExpiresIn = now.plus(jwtProperties.accessTokenValidityInSeconds(), ChronoUnit.SECONDS);

        String authoritiesString = String.join(",", authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toList()
        );

        log.debug("Access Token 생성 - userId={}, authorities={}, expiresIn={}", userId, authoritiesString, accessTokenExpiresIn);

        return Jwts.builder()
                .subject(email)
                .claim(USER_ID_KEY, userId)
                .claim(AUTHORITIES_KEY, authoritiesString)
                .issuedAt(Date.from(now))
                .expiration(Date.from(accessTokenExpiresIn))
                .signWith(secretKey)
                .compact();
    }

    @Override
    public String createRefreshToken(String email) {
        Instant now = Instant.now();
        Instant refreshTokenExpiresIn = now.plus(jwtProperties.refreshTokenValidityInSeconds(), ChronoUnit.SECONDS);

        log.debug("Refresh Token 생성 - expiresIn={}", refreshTokenExpiresIn);

        return Jwts.builder()
                .subject(email)
                .issuedAt(Date.from(now))
                .expiration(Date.from(refreshTokenExpiresIn))
                .signWith(secretKey)
                .compact();
    }
}
