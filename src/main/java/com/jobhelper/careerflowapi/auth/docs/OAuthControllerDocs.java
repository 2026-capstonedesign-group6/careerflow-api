package com.jobhelper.careerflowapi.auth.docs;

import com.jobhelper.careerflowapi.auth.docs.error.OAuthLoginErrorDocs;
import com.jobhelper.careerflowapi.auth.presentation.dto.request.OAuthCallbackRequest;
import com.jobhelper.careerflowapi.auth.presentation.dto.response.LoginResponse;
import com.jobhelper.careerflowapi.global.domain.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;

@Tag(name = "OAuth2", description = "소셜 로그인 API")
public interface OAuthControllerDocs {

    @Operation(
            summary = "소셜 로그인 인가 URL 리다이렉트",
            description = "provider의 OAuth2 로그인 페이지로 리다이렉트합니다. (kakao / google)"
    )
    void authorize(
            @Parameter(description = "OAuth2 Provider (kakao, google)", example = "kakao")
            @PathVariable String provider,
            HttpServletResponse response
    ) throws IOException;

    @Operation(
            summary = "소셜 로그인 콜백",
            description = "Provider로부터 받은 Authorization Code로 로그인 또는 자동 회원가입을 처리합니다."
    )
    @OAuthLoginErrorDocs
    ResponseEntity<CommonResponse<LoginResponse>> callback(
            @Parameter(description = "OAuth2 Provider (kakao, google)", example = "kakao")
            @PathVariable String provider,
            @RequestBody @Valid OAuthCallbackRequest request
    );
}