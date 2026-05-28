package com.jobhelper.careerflowapi.user.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record StarRequest(
        @Schema(example = "팀 프로젝트 마감 2주 전 핵심 팀원이 이탈하는 상황이 발생했습니다.") String situation,
        @Schema(example = "프로젝트를 기한 내에 완료해야 하는 과제가 주어졌습니다.") String task,
        @Schema(example = "남은 팀원들과 역할을 재분배하고 일정을 조정했습니다.") String action,
        @Schema(example = "예정된 기한 내에 프로젝트를 완료하여 A+ 학점을 받았습니다.") String result
) {}
