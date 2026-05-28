package com.jobhelper.careerflowapi.user.presentation.dto.response;

import com.jobhelper.careerflowapi.user.domain.entity.Training;

import java.time.LocalDate;

public record TrainingResponse(
        Long id,
        String courseName,
        String instituteName,
        LocalDate startDate,
        LocalDate endDate
) {
    public static TrainingResponse from(Training entity) {
        return new TrainingResponse(
                entity.getId(),
                entity.getCourseName(),
                entity.getInstituteName(),
                entity.getStartDate(),
                entity.getEndDate()
        );
    }
}
