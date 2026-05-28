package com.jobhelper.careerflowapi.user.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record CareerRequest(
        @NotBlank
        @Schema(example = "네이버")
        String companyName,

        @Schema(example = "백엔드 인턴")
        String position,

        @Schema(example = "Spring Boot 기반 API 개발")
        String description,

        @Schema(example = "2023-07-01")
        LocalDate startDate,

        @Schema(example = "2023-08-31")
        LocalDate endDate,

        @Schema(example = "false")
        boolean isCurrent
) {
}
