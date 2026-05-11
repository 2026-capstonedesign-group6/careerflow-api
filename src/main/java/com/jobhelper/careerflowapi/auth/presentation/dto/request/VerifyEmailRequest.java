package com.jobhelper.careerflowapi.auth.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerifyEmailRequest(
        @NotBlank @Email
        @Schema(example = "user@example.com")
        String email,

        @NotBlank @Pattern(regexp = "\\d{6}", message = "인증 코드는 6자리 숫자여야 합니다.")
        @Schema(example = "483921")
        String code
) {}
