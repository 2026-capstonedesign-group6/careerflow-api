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
        description = "로그인 실패",
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CommonResponse.class),
                examples = {
                        @ExampleObject(
                                name = "존재하지 않는 사용자",
                                value = """
                                        {
                                            "result": "FAIL",
                                            "code": "U004",
                                            "message": "일치하는 회원 정보가 존재하지 않습니다.",
                                            "timestamp": "2026-01-17 12:00:00"
                                        }
                                        """
                        ),
                        @ExampleObject(
                                name = "비밀번호 불일치",
                                value = """
                                        {
                                            "result": "FAIL",
                                            "code": "A012",
                                            "message": "비밀번호가 일치하지 않습니다.",
                                            "timestamp": "2026-01-17 12:00:00"
                                        }
                                        """
                        )
                }
        )
)
public @interface AuthLoginErrorDocs {
}