package com.jobhelper.careerflowapi.resume.presentation.dto.response;

import com.jobhelper.careerflowapi.resume.domain.entity.EssayItem;

public record EssayItemResponse(
        Long id,
        String question,
        String content,
        int orderIndex
) {
    public static EssayItemResponse from(EssayItem essay) {
        return new EssayItemResponse(
                essay.getId(),
                essay.getQuestion(),
                essay.getContent(),
                essay.getOrderIndex()
        );
    }
}
