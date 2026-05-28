package com.jobhelper.careerflowapi.user.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record OtherActivityRequest(
        @Schema(example = "교내 봉사활동 참여") String content,
        @Schema(example = "한국대학교") String organizationName,
        @Schema(example = "2023-03-01") LocalDate startDate,
        @Schema(example = "2023-06-30") LocalDate endDate
) {}
