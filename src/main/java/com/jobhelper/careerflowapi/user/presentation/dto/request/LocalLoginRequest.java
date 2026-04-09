package com.jobhelper.careerflowapi.user.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LocalLoginRequest(
        @NotBlank @Email
        @Schema(example = "user@example.com")
        String email,

        @NotBlank
        @Schema(example = "password1234!")
        String password
) {
}
