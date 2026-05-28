package com.jobhelper.careerflowapi.user.presentation.dto.request;

import com.jobhelper.careerflowapi.user.domain.enums.EducationLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record EducationRequest(
        @NotBlank
        @Schema(example = "한국대학교")
        String schoolName,

        @Schema(example = "컴퓨터공학")
        String major,

        @NotNull
        @Schema(example = "BACHELOR")
        EducationLevel level,

        @Schema(example = "2018-03-01")
        LocalDate startDate,

        @Schema(example = "2022-02-28")
        LocalDate endDate,

        @Schema(example = "false")
        boolean isAttending
) {
}
