package com.jobhelper.careerflowapi.user.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record MilitaryRequest(
        @Schema(example = "2020-03-02") LocalDate startDate,
        @Schema(example = "2021-09-01") LocalDate endDate,
        @Schema(example = "육군 병장") String militaryType,
        @Schema(example = "") String exemptionReason
) {}
