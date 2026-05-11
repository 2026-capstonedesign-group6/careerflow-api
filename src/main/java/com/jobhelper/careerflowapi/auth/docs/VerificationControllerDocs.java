package com.jobhelper.careerflowapi.auth.docs;

import com.jobhelper.careerflowapi.auth.docs.error.VerificationErrorDocs;
import com.jobhelper.careerflowapi.auth.presentation.dto.request.ResendVerificationRequest;
import com.jobhelper.careerflowapi.auth.presentation.dto.request.VerifyEmailRequest;
import com.jobhelper.careerflowapi.global.domain.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Auth", description = "인증 API")
public interface VerificationControllerDocs {

    @Operation(summary = "이메일 인증 코드 확인")
    @VerificationErrorDocs
    ResponseEntity<CommonResponse<Void>> verify(@RequestBody @Valid VerifyEmailRequest request);

    @Operation(summary = "이메일 인증 코드 재발송")
    @VerificationErrorDocs
    ResponseEntity<CommonResponse<Void>> resend(@RequestBody @Valid ResendVerificationRequest request);
}