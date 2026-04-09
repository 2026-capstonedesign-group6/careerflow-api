package com.jobhelper.careerflowapi.auth.docs.error;

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
        responseCode = "401",
        description = "토큰 갱신 실패",
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CommonResponse.class),
                examples = {
                        @ExampleObject(
                                name = "리프레시 토큰 없음",
                                value = """
                                        {
                                            "result": "FAIL",
                                            "code": "A020",
                                            "message": "유효하지 않은 리프레시 토큰입니다.",
                                            "timestamp": "2026-01-17 12:00:00"
                                        }
                                        """
                        ),
                        @ExampleObject(
                                name = "로그아웃 상태 (Redis에 토큰 없음)",
                                value = """
                                        {
                                            "result": "FAIL",
                                            "code": "A021",
                                            "message": "리프레시 토큰을 찾을 수 없습니다. (로그아웃 되었습니다)",
                                            "timestamp": "2026-01-17 12:00:00"
                                        }
                                        """
                        ),
                        @ExampleObject(
                                name = "토큰 탈취 감지",
                                value = """
                                        {
                                            "result": "FAIL",
                                            "code": "A023",
                                            "message": "토큰 탈취가 감지되었습니다. 보안을 위해 재로그인이 필요합니다.",
                                            "timestamp": "2026-01-17 12:00:00"
                                        }
                                        """
                        ),
                        @ExampleObject(
                                name = "리프레시 토큰 변조",
                                value = """
                                        {
                                            "result": "FAIL",
                                            "code": "A003",
                                            "message": "잘못된 JWT 토큰입니다.",
                                            "timestamp": "2026-01-17 12:00:00"
                                        }
                                        """
                        ),
                        @ExampleObject(
                                name = "리프레시 토큰 만료",
                                value = """
                                        {
                                            "result": "FAIL",
                                            "code": "A004",
                                            "message": "만료된 JWT 토큰입니다.",
                                            "timestamp": "2026-01-17 12:00:00"
                                        }
                                        """
                        ),
                        @ExampleObject(
                                name = "리프레시 토큰 형식 오류",
                                value = """
                                        {
                                            "result": "FAIL",
                                            "code": "A005",
                                            "message": "유효하지 않은 JWT 토큰입니다.",
                                            "timestamp": "2026-01-17 12:00:00"
                                        }
                                        """
                        )
                }
        )
)
public @interface AuthRefreshErrorDocs {
}