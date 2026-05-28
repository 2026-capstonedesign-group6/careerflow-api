package com.jobhelper.careerflowapi.user.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record FamilyMemberRequest(
        @Schema(example = "부") String relation,
        @Schema(example = "홍판서") String name,
        @Schema(example = "1965") String birthYear,
        @Schema(example = "회사원") String occupation
) {}
