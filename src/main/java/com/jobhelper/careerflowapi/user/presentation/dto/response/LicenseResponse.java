package com.jobhelper.careerflowapi.user.presentation.dto.response;

import com.jobhelper.careerflowapi.user.domain.entity.License;

public record LicenseResponse(
        Long id,
        String licenseName,
        String licenseNumber,
        String grade,
        String issuingOrganization
) {
    public static LicenseResponse from(License entity) {
        return new LicenseResponse(
                entity.getId(),
                entity.getLicenseName(),
                entity.getLicenseNumber(),
                entity.getGrade(),
                entity.getIssuingOrganization()
        );
    }
}
