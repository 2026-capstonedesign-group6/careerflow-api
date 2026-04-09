package com.jobhelper.careerflowapi.user.docs.error;

import com.jobhelper.careerflowapi.global.domain.dto.CommonResponse;
import com.jobhelper.careerflowapi.global.docs.errors.CommonInternalServerErrorResponseDocs;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@CommonInternalServerErrorResponseDocs
@ApiResponse(
        responseCode = "400",
        description = "회원가입 실패",
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CommonResponse.class),
                examples = {
                        @ExampleObject(
                                name = "이메일 중복",
                                value = """
                                        {
                                            "result": "FAIL",
                                            "code": "U001",
                                            "message": "이미 존재하는 이메일입니다.",
                                            "timestamp": "2026-01-17 12:00:00"
                                        }
                                        """
                        ),
                        @ExampleObject(
                                name = "닉네임 중복",
                                value = """
                                        {
                                            "result": "FAIL",
                                            "code": "U002",
                                            "message": "이미 존재하는 닉네임입니다.",
                                            "timestamp": "2026-01-17 12:00:00"
                                        }
                                        """
                        ),
                        @ExampleObject(
                                name = "유효성 검증 실패",
                                value = """
                                        {
                                            "result": "FAIL",
                                            "code": "G004",
                                            "message": "잘못된 입력입니다.",
                                            "data": [
                                                { "field": "email", "reason": "올바른 이메일 형식이 아닙니다." },
                                                { "field": "password", "reason": "크기가 8에서 50 사이여야 합니다" }
                                            ],
                                            "timestamp": "2026-01-17 12:00:00"
                                        }
                                        """
                        ),
                        @ExampleObject(
                                name = "인증 메일 재발송 횟수 초과",
                                value = """
                                        {
                                            "result": "FAIL",
                                            "code": "A009",
                                            "message": "요청 횟수가 너무 많습니다. 잠시 후 다시 시도해주세요.",
                                            "timestamp": "2026-01-17 12:00:00"
                                        }
                                        """
                        )
                }
        )
)
public @interface AuthSignupErrorDocs {
}
