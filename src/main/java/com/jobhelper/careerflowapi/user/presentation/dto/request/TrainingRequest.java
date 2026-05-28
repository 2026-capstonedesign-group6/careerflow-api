package com.jobhelper.careerflowapi.user.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record TrainingRequest(
        @Schema(example = "스프링 부트 심화 과정") String courseName,
        @Schema(example = "멀티캠퍼스") String instituteName,
        @Schema(example = "2023-01-02") LocalDate startDate,
        @Schema(example = "2023-02-28") LocalDate endDate
) {}
