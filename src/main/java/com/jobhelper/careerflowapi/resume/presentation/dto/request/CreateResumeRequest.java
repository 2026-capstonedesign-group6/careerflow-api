package com.jobhelper.careerflowapi.resume.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record CreateResumeRequest(
        @NotBlank
        @Schema(example = "네이버 공채 지원")
        String title,

        @Min(1) @Max(6)
        @Schema(example = "6")
        int stepProgress,

        @Valid
        List<EssayItemRequest> essays
) {
}
