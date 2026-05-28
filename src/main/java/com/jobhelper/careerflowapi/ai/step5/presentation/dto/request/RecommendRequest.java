package com.jobhelper.careerflowapi.ai.step5.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record RecommendRequest(
        @NotBlank String question,
        @NotEmpty List<Long> experienceIds
) {}
