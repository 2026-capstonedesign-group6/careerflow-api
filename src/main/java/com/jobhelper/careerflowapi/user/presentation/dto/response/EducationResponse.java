package com.jobhelper.careerflowapi.user.presentation.dto.response;

import com.jobhelper.careerflowapi.user.domain.entity.Education;
import com.jobhelper.careerflowapi.user.domain.enums.EducationLevel;

import java.time.LocalDate;

public record EducationResponse(
        Long id,
        String schoolName,
        String major,
        EducationLevel level,
        LocalDate startDate,
        LocalDate endDate,
        boolean isAttending
) {
    public static EducationResponse from(Education education) {
        return new EducationResponse(
                education.getId(),
                education.getSchoolName(),
                education.getMajor(),
                education.getLevel(),
                education.getStartDate(),
                education.getEndDate(),
                education.isAttending()
        );
    }
}
