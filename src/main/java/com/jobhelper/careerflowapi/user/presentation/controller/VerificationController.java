package com.jobhelper.careerflowapi.user.presentation.controller;

import com.jobhelper.careerflowapi.global.domain.dto.CommonResponse;
import com.jobhelper.careerflowapi.user.application.VerificationService;
import com.jobhelper.careerflowapi.user.docs.VerificationControllerDocs;
import com.jobhelper.careerflowapi.user.presentation.dto.request.ResendVerificationRequest;
import com.jobhelper.careerflowapi.user.presentation.dto.request.VerifyEmailRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/verify")
@RequiredArgsConstructor
public class VerificationController implements VerificationControllerDocs {

    private final VerificationService verificationService;

    @Override
    @PostMapping("/email")
    public ResponseEntity<CommonResponse<Void>> verify(@RequestBody @Valid VerifyEmailRequest request) {
        verificationService.verify(request.email(), request.code());
        return ResponseEntity.ok(CommonResponse.onSuccess());
    }

    @Override
    @PostMapping("/email/resend")
    public ResponseEntity<CommonResponse<Void>> resend(@RequestBody @Valid ResendVerificationRequest request) {
        verificationService.resend(request.email());
        return ResponseEntity.ok(CommonResponse.onSuccess());
    }
}
