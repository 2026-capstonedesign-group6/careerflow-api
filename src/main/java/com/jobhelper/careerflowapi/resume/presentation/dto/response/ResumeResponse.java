package com.jobhelper.careerflowapi.resume.presentation.dto.response;

import com.jobhelper.careerflowapi.resume.domain.entity.Resume;

import java.time.LocalDateTime;
import java.util.List;

public record ResumeResponse(
        Long id,
        String title,
        int stepProgress,
        List<EssayItemResponse> essays,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ResumeResponse from(Resume resume) {
        return new ResumeResponse(
                resume.getId(),
                resume.getTitle(),
                resume.getStepProgress(),
                resume.getEssays().stream().map(EssayItemResponse::from).toList(),
                resume.getCreatedAt(),
                resume.getUpdatedAt()
        );
    }
}
