package com.jobhelper.careerflowapi.resume.presentation.dto.response;

import com.jobhelper.careerflowapi.global.domain.CursorProjection;
import com.jobhelper.careerflowapi.resume.domain.entity.Resume;

import java.time.LocalDateTime;

public record ResumeSummaryResponse(
        Long id,
        String title,
        int stepProgress,
        LocalDateTime createdAt
) implements CursorProjection {

    @Override
    public Long getId() {
        return id;
    }

    public static ResumeSummaryResponse from(Resume resume) {
        return new ResumeSummaryResponse(
                resume.getId(),
                resume.getTitle(),
                resume.getStepProgress(),
                resume.getCreatedAt()
        );
    }
}
