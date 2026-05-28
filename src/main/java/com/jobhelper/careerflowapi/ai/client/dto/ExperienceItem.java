package com.jobhelper.careerflowapi.ai.client.dto;

public record ExperienceItem(
        Long id,
        String title,
        String situation,
        String task,
        String action,
        String result
) {}
