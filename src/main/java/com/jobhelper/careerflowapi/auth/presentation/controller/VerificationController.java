package com.jobhelper.careerflowapi.auth.presentation.controller;

import com.jobhelper.careerflowapi.auth.command.AccountCommandHandler;
import com.jobhelper.careerflowapi.auth.docs.VerificationControllerDocs;
import com.jobhelper.careerflowapi.auth.presentation.dto.request.ResendVerificationRequest;
import com.jobhelper.careerflowapi.auth.presentation.dto.request.VerifyEmailRequest;
import com.jobhelper.careerflowapi.global.domain.dto.CommonResponse;
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

    private final AccountCommandHandler commandHandler;

    @Override
    @PostMapping("/email")
    public ResponseEntity<CommonResponse<Void>> verify(@RequestBody @Valid VerifyEmailRequest request) {
        commandHandler.verifyEmail(request.email(), request.code());
        return ResponseEntity.ok(CommonResponse.onSuccess());
    }

    @Override
    @PostMapping("/email/resend")
    public ResponseEntity<CommonResponse<Void>> resend(@RequestBody @Valid ResendVerificationRequest request) {
        commandHandler.resendVerification(request.email());
        return ResponseEntity.ok(CommonResponse.onSuccess());
    }
}
