package com.jobhelper.careerflowapi.user.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record AwardRequest(
        @Schema(example = "캡스톤디자인 최우수상") String awardTitle,
        @Schema(example = "한국대학교") String organizationName,
        @Schema(example = "2024-06-01") LocalDate startDate,
        @Schema(example = "2024-06-01") LocalDate endDate
) {}
