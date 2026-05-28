package com.jobhelper.careerflowapi.experience.presentation.dto.response;

import com.jobhelper.careerflowapi.experience.domain.entity.Experience;
import com.jobhelper.careerflowapi.global.domain.CursorProjection;

import java.time.LocalDate;

public record ExperienceSummaryResponse(
        Long id,
        String title,
        String category,
        LocalDate activityDate
) implements CursorProjection {

    @Override
    public Long getId() {
        return id;
    }

    public static ExperienceSummaryResponse from(Experience experience) {
        return new ExperienceSummaryResponse(
                experience.getId(),
                experience.getTitle(),
                experience.getCategory(),
                experience.getActivityDate()
        );
    }
}
