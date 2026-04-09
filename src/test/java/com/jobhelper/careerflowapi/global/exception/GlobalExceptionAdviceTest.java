package com.jobhelper.careerflowapi.global.exception;

import com.jobhelper.careerflowapi.global.domain.ErrorCode;
import com.jobhelper.careerflowapi.global.domain.dto.CommonResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionAdviceTest {

    private final GlobalExceptionAdvice advice = new GlobalExceptionAdvice();
    private final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/test");

    @Test
    void handleBusinessException_에러코드_상태와_메시지로_응답한다() {
        BusinessException ex = new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);

        ResponseEntity<CommonResponse<Void>> response = advice.handleBusinessException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("U001");
        assertThat(response.getBody().result()).isEqualTo(CommonResponse.Result.FAIL);
    }

    @Test
    void handleBusinessException_상세_메시지가_있으면_포함된다() {
        BusinessException ex = new BusinessException(ErrorCode.USER_NOT_FOUND, "이메일에 해당하는 사용자 없음");

        ResponseEntity<CommonResponse<Void>> response = advice.handleBusinessException(ex, request);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).contains("이메일에 해당하는 사용자 없음");
    }

    @Test
    void handleAuthorizationException_401_상태로_응답한다() {
        AuthorizationException ex = new AuthorizationException(ErrorCode.JWT_EXPIRED);

        ResponseEntity<CommonResponse<Void>> response = advice.handleAuthorizationException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("A004");
    }

    @Test
    void handleAuthorizationException_토큰_탈취_감지시_401을_반환한다() {
        AuthorizationException ex = new AuthorizationException(ErrorCode.REFRESH_TOKEN_THEFT_DETECTED);

        ResponseEntity<CommonResponse<Void>> response = advice.handleAuthorizationException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().code()).isEqualTo("A023");
    }

    @Test
    void handleConstraintViolationException_필드_오류_목록을_반환한다() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        jakarta.validation.Path path = mock(jakarta.validation.Path.class);
        when(violation.getPropertyPath()).thenReturn(path);
        when(path.toString()).thenReturn("email");
        when(violation.getMessage()).thenReturn("이메일 형식이 올바르지 않습니다");
        ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation));

        ResponseEntity<?> response = advice.handleConstraintViolationException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void handleBadRequest_NoHandlerFoundException은_NOT_FOUND로_응답한다() {
        NoHandlerFoundException ex = new NoHandlerFoundException("GET", "/unknown", null);

        ResponseEntity<CommonResponse<Void>> response = advice.handleBadRequest(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().code()).isEqualTo("G006");
    }

    @Test
    void handleBadRequest_HttpRequestMethodNotSupportedException은_METHOD_NOT_ALLOWED로_응답한다() {
        HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException("DELETE");

        ResponseEntity<CommonResponse<Void>> response = advice.handleBadRequest(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
        assertThat(response.getBody().code()).isEqualTo("G005");
    }

    @Test
    void handleBadRequest_IllegalArgumentException은_INVALID_INPUT_VALUE로_응답한다() {
        IllegalArgumentException ex = new IllegalArgumentException("잘못된 입력");

        ResponseEntity<CommonResponse<Void>> response = advice.handleBadRequest(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().code()).isEqualTo("G004");
    }

    @Test
    void handleAuthenticationException_401로_응답한다() {
        AuthenticationException ex = mock(AuthenticationException.class);

        ResponseEntity<CommonResponse<Void>> response = advice.handleAuthenticationException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().code()).isEqualTo("A001");
    }

    @Test
    void handleAccessDeniedException_403으로_응답한다() {
        AccessDeniedException ex = new AccessDeniedException("접근 거부");

        ResponseEntity<CommonResponse<Void>> response = advice.handleAccessDeniedException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody().code()).isEqualTo("A002");
    }

    @Test
    void handleException_예상치_못한_예외는_500으로_응답한다() {
        Exception ex = new RuntimeException("예상치 못한 오류");

        ResponseEntity<CommonResponse<Void>> response = advice.handleException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().code()).isEqualTo("G001");
    }
}
