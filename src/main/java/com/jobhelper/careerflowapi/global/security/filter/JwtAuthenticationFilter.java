package com.jobhelper.careerflowapi.global.security.filter;

import com.jobhelper.careerflowapi.global.exception.AuthorizationException;
import com.jobhelper.careerflowapi.global.security.jwt.JwtTokenValidator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenValidator jwtTokenValidator;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private final List<String> excludeUrls = List.of(
            "/favicon.ico",
            "/api/auth/signup/local",
            "/api/auth/login/local",
            "/api/auth/logout",
            "/api/auth/refresh",
            "/api/auth/oauth2/authorization/**",
            "/api/auth/oauth2/callback/**"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return excludeUrls.stream()
                .anyMatch(url -> pathMatcher.match(url, request.getRequestURI()));
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {
        Optional<String> tokenOpt = jwtTokenValidator.resolveToken(request);

        if (tokenOpt.isPresent()) {
            String token = tokenOpt.get();
            try {
                Authentication authentication = jwtTokenValidator.getAuthentication(token);

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);

                log.info("Security Context 내에 인증 정보 저장 - Name = {}, uri: {}",
                        authentication.getName(), request.getRequestURI()
                );
            } catch (AuthorizationException e) {
                request.setAttribute("exception", e);

                log.warn("인증 실패 - uri: {}, error: {}",
                        request.getRequestURI(), e.getMessage()
                );
            }
        }

        filterChain.doFilter(request, response);
    }
}
