package com.jobhelper.careerflowapi.ai.client.dto;

import java.util.List;

public record EssayGenerationAiRequest(
        String question,
        List<ExperienceItem> experiences,
        int targetLength,
        String tone
) {}
