package com.jobhelper.careerflowapi.auth.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LocalSignupRequest(
        @NotBlank @Email
        @Schema(example = "user@example.com")
        String email,

        @NotBlank @Size(min = 2, max = 20)
        @Schema(example = "홍길동")
        String nickname,

        @NotBlank @Size(min = 8, max = 50)
        @Schema(example = "password1234!")
        String password
) {
}
