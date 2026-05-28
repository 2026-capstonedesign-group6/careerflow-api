package com.jobhelper.careerflowapi.user.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record TechStackRequest(
        @Schema(example = "Spring Boot") String skill
) {}
