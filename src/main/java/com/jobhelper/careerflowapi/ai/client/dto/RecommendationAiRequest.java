package com.jobhelper.careerflowapi.ai.client.dto;

import java.util.List;

public record RecommendationAiRequest(String question, List<ExperienceItem> experiences) {}
