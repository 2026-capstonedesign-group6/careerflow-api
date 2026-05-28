package com.jobhelper.careerflowapi.ai.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ClaudeRequest(
        String model,
        @JsonProperty("max_tokens") int maxTokens,
        List<ClaudeMessage> messages,
        String system
) {}
