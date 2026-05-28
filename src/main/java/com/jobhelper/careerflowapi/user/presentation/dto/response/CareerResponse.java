package com.jobhelper.careerflowapi.user.presentation.dto.response;

import com.jobhelper.careerflowapi.user.domain.entity.Career;

import java.time.LocalDate;

public record CareerResponse(
        Long id,
        String companyName,
        String position,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        boolean isCurrent
) {
    public static CareerResponse from(Career career) {
        return new CareerResponse(
                career.getId(),
                career.getCompanyName(),
                career.getPosition(),
                career.getDescription(),
                career.getStartDate(),
                career.getEndDate(),
                career.isCurrent()
        );
    }
}
