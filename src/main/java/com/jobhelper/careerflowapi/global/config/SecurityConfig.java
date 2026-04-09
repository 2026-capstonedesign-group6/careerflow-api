package com.jobhelper.careerflowapi.global.config;

import com.jobhelper.careerflowapi.global.security.filter.JwtAuthenticationFilter;
import com.jobhelper.careerflowapi.global.security.filter.SecurityAuditLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;
    private final SecurityExceptionConfig securityExceptionConfig;
    private final SecurityAuditLogger securityAuditLogger;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final String[] PUBLIC_URLS = {
            "/favicon.ico",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/api/auth/signup/local",
            "/api/auth/login/local",
            "/api/auth/logout",
            "/api/auth/refresh",
            "/api/auth/oauth2/authorization/**",
            "/api/auth/oauth2/callback/**"
    };

    private static final String[] API_URLS = {
            "/api/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors ->
                        cors.configurationSource(corsConfigurationSource)
                )
                .sessionManagement(
                        session ->
                                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
//                      // Https 배포시
//                      .httpStrictTransportSecurity(hsts -> hsts
//                                      .includeSubDomains(true)
//                                      .maxAgeInSeconds(31536000)
//                      )
//                      // CSP는 프론트엔드와 백엔드가 동일한 도메인에서 운영되고, XSS 공격 위험이 낮은 경우에만 완화된 정책으로 설정하는 것을 권장
//                      .contentSecurityPolicy(csp -> csp
//                              .policyDirectives("default-src 'self'; frame-ancestors 'self';"
//                      )
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                        .contentTypeOptions(Customizer.withDefaults())
                        .cacheControl(HeadersConfigurer.CacheControlConfig::disable)
                        .referrerPolicy(rp -> rp
                                        .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER)
                        )
                        .permissionsPolicyHeader(pp -> pp
                                        .policy("geolocation=(), microphone=(), camera=()")
                        )
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_URLS).permitAll()
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/actuator/**").hasRole("ADMIN")
                        .requestMatchers(API_URLS).authenticated()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(securityExceptionConfig::configure)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(securityAuditLogger, JwtAuthenticationFilter.class)
        ;

        return http.build();
    }
}
