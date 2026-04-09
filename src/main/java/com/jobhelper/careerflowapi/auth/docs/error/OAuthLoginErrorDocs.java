package com.jobhelper.careerflowapi.auth.docs.error;

import com.jobhelper.careerflowapi.global.domain.dto.CommonResponse;
import com.jobhelper.careerflowapi.global.docs.errors.CommonBadRequestResponseDocs;
import com.jobhelper.careerflowapi.global.docs.errors.CommonInternalServerErrorResponseDocs;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses({
        @ApiResponse(responseCode = "400", description = "A011: 지원하지 않는 Provider / A010: 소셜 로그인 실패 / U001: 이미 가입된 이메일",
                content = @Content(schema = @Schema(implementation = CommonResponse.class))),
        @ApiResponse(responseCode = "502", description = "G002: Provider 서버 통신 오류",
                content = @Content(schema = @Schema(implementation = CommonResponse.class))),
})
@CommonBadRequestResponseDocs
@CommonInternalServerErrorResponseDocs
public @interface OAuthLoginErrorDocs {}