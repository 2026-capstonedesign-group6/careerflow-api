package com.jobhelper.careerflowapi.user.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record OAuthCallbackRequest(
        @NotBlank
        @Schema(example = "authorization_code_from_provider", description = "Provider로부터 받은 Authorization Code")
        String code
) {}
