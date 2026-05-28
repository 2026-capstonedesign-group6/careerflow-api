package com.jobhelper.careerflowapi.ai.client.dto;

import java.util.List;

public record RecommendationAiResponse(List<RecommendedExperience> recommendations) {

    public record RecommendedExperience(Long experienceId, int relevance) {}
}
