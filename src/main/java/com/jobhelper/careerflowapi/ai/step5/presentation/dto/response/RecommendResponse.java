package com.jobhelper.careerflowapi.ai.step5.presentation.dto.response;

import java.util.List;

public record RecommendResponse(List<RecommendationItem> recommendations) {

    public record RecommendationItem(Long experienceId, String title, int relevance) {}
}
