package com.jobhelper.careerflowapi.user.presentation.dto.response;

import com.jobhelper.careerflowapi.user.domain.entity.Award;

import java.time.LocalDate;

public record AwardResponse(
        Long id,
        String awardTitle,
        String organizationName,
        LocalDate startDate,
        LocalDate endDate
) {
    public static AwardResponse from(Award entity) {
        return new AwardResponse(
                entity.getId(),
                entity.getAwardTitle(),
                entity.getOrganizationName(),
                entity.getStartDate(),
                entity.getEndDate()
        );
    }
}
