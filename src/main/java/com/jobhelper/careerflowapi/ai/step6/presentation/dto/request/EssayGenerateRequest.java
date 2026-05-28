package com.jobhelper.careerflowapi.ai.step6.presentation.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record EssayGenerateRequest(
        @NotBlank String question,
        @NotEmpty List<Long> experienceIds,
        @Min(100) @Max(2000) int targetLength,
        @NotNull WritingTone tone
) {}
