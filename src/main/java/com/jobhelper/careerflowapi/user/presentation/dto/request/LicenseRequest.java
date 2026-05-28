package com.jobhelper.careerflowapi.user.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record LicenseRequest(
        @Schema(example = "정보처리기사") String licenseName,
        @Schema(example = "24-123456") String licenseNumber,
        @Schema(example = "1급") String grade,
        @Schema(example = "한국산업인력공단") String issuingOrganization
) {}
