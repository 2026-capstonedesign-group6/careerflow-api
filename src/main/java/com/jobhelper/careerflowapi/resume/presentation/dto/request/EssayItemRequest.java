package com.jobhelper.careerflowapi.resume.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record EssayItemRequest(
        @NotBlank
        @Schema(example = "지원 동기를 서술하시오.")
        String question,

        @Schema(example = "저는 2024년 캡스톤 프로젝트에서...")
        String content,

        @Schema(example = "0")
        int orderIndex
) {
}
