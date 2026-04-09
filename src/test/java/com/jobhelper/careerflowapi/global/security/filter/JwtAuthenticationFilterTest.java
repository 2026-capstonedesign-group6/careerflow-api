package com.jobhelper.careerflowapi.global.security.filter;

import com.jobhelper.careerflowapi.global.domain.ErrorCode;
import com.jobhelper.careerflowapi.global.exception.AuthorizationException;
import com.jobhelper.careerflowapi.global.security.dto.AuthUser;
import com.jobhelper.careerflowapi.global.security.jwt.JwtTokenValidator;
import com.jobhelper.careerflowapi.global.security.principal.PrincipalDetails;
import com.jobhelper.careerflowapi.global.security.token.JwtAuthenticationToken;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenValidator jwtTokenValidator;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ==================== shouldNotFilter ====================

    @Test
    void shouldNotFilter_로그인_경로는_필터를_건너뛴다() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/auth/login/local");

        assertThat(filter.shouldNotFilter(request)).isTrue();
    }

    @Test
    void shouldNotFilter_회원가입_경로는_필터를_건너뛴다() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/auth/signup/local");

        assertThat(filter.shouldNotFilter(request)).isTrue();
    }

    @Test
    void shouldNotFilter_OAuth_콜백_경로는_필터를_건너뛴다() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/auth/oauth2/callback/kakao");

        assertThat(filter.shouldNotFilter(request)).isTrue();
    }

    @Test
    void shouldNotFilter_일반_API_경로는_필터를_수행한다() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users/me");

        assertThat(filter.shouldNotFilter(request)).isFalse();
    }

    // ==================== doFilterInternal ====================

    @Test
    void doFilterInternal_유효한_토큰이면_SecurityContext에_인증_정보를_설정한다() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AuthUser authUser = AuthUser.of(1L, "user@test.com", "ROLE_USER");
        PrincipalDetails principal = PrincipalDetails.from(authUser);
        JwtAuthenticationToken auth = new JwtAuthenticationToken(principal, "valid-token",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        given(jwtTokenValidator.resolveToken(request)).willReturn(Optional.of("valid-token"));
        given(jwtTokenValidator.getAuthentication("valid-token")).willReturn(auth);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("user@test.com");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_토큰이_없으면_SecurityContext를_설정하지_않는다() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        given(jwtTokenValidator.resolveToken(request)).willReturn(Optional.empty());

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_유효하지_않은_토큰이면_request_attribute에_예외를_설정한다() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AuthorizationException exception = new AuthorizationException(ErrorCode.JWT_EXPIRED);

        given(jwtTokenValidator.resolveToken(request)).willReturn(Optional.of("expired-token"));
        given(jwtTokenValidator.getAuthentication("expired-token")).willThrow(exception);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(request.getAttribute("exception")).isSameAs(exception);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }
}
