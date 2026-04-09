package com.jobhelper.careerflowapi.user.docs;

import com.jobhelper.careerflowapi.global.domain.dto.CommonResponse;
import com.jobhelper.careerflowapi.user.docs.error.AuthLoginErrorDocs;
import com.jobhelper.careerflowapi.user.docs.error.AuthRefreshErrorDocs;
import com.jobhelper.careerflowapi.user.docs.error.AuthSignupErrorDocs;
import com.jobhelper.careerflowapi.user.presentation.dto.request.LocalLoginRequest;
import com.jobhelper.careerflowapi.user.presentation.dto.request.LocalSignupRequest;
import com.jobhelper.careerflowapi.user.presentation.dto.response.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Auth", description = "인증 API")
public interface AuthControllerDocs {

    @Operation(summary = "로컬 회원가입")
    @AuthSignupErrorDocs
    ResponseEntity<CommonResponse<Void>> localSignup(@RequestBody @Valid LocalSignupRequest request);

    @Operation(summary = "로컬 로그인")
    @AuthLoginErrorDocs
    ResponseEntity<CommonResponse<LoginResponse>> localLogin(@RequestBody @Valid LocalLoginRequest request);

    @Operation(summary = "토큰 갱신 (RTR)")
    @AuthRefreshErrorDocs
    ResponseEntity<CommonResponse<LoginResponse>> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    );

    @Operation(summary = "로그아웃")
    ResponseEntity<CommonResponse<Void>> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    );
}
