package com.jobhelper.careerflowapi.experience.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record CreateExperienceRequest(
        @NotBlank
        @Schema(example = "캡스톤 팀 리더 경험")
        String title,

        @Schema(example = "팀 프로젝트에서 의사소통 부재 문제가 발생하였습니다.")
        String situation,

        @Schema(example = "팀장으로서 협업 체계를 개선해야 했습니다.")
        String task,

        @Schema(example = "주 2회 스탠드업 미팅을 도입하고 역할을 명확히 분배했습니다.")
        String action,

        @Schema(example = "일정을 100% 준수하며 프로젝트를 성공적으로 완료했습니다.")
        String result,

        @Schema(example = "팀플")
        String category,

        @Schema(example = "2024-06-01")
        LocalDate activityDate
) {
}
