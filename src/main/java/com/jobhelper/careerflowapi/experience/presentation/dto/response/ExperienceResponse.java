package com.jobhelper.careerflowapi.experience.presentation.dto.response;

import com.jobhelper.careerflowapi.experience.domain.entity.Experience;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ExperienceResponse(
        Long id,
        String title,
        String situation,
        String task,
        String action,
        String result,
        String category,
        LocalDate activityDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ExperienceResponse from(Experience experience) {
        return new ExperienceResponse(
                experience.getId(),
                experience.getTitle(),
                experience.getSituation(),
                experience.getTask(),
                experience.getAction(),
                experience.getResult(),
                experience.getCategory(),
                experience.getActivityDate(),
                experience.getCreatedAt(),
                experience.getUpdatedAt()
        );
    }
}
