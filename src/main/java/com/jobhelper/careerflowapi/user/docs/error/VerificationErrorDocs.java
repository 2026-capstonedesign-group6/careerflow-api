package com.jobhelper.careerflowapi.user.docs.error;

import com.jobhelper.careerflowapi.global.domain.dto.CommonResponse;
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
        @ApiResponse(responseCode = "400", description = """
                A013: 이미 인증 완료 /
                A014: 인증 코드 만료 /
                A015: 인증 코드 불일치 /
                U004: 사용자 없음
                """,
                content = @Content(schema = @Schema(implementation = CommonResponse.class)))
})
@CommonInternalServerErrorResponseDocs
public @interface VerificationErrorDocs {}