package com.jobhelper.careerflowapi.user.presentation.dto.response;

import com.jobhelper.careerflowapi.user.domain.entity.OtherActivity;

import java.time.LocalDate;

public record OtherActivityResponse(
        Long id,
        String content,
        String organizationName,
        LocalDate startDate,
        LocalDate endDate
) {
    public static OtherActivityResponse from(OtherActivity entity) {
        return new OtherActivityResponse(
                entity.getId(),
                entity.getContent(),
                entity.getOrganizationName(),
                entity.getStartDate(),
                entity.getEndDate()
        );
    }
}
