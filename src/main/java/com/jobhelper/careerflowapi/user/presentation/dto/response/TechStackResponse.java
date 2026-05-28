package com.jobhelper.careerflowapi.user.presentation.dto.response;

import com.jobhelper.careerflowapi.user.domain.entity.TechStack;

public record TechStackResponse(
        Long id,
        String skill
) {
    public static TechStackResponse from(TechStack entity) {
        return new TechStackResponse(
                entity.getId(),
                entity.getSkill()
        );
    }
}
