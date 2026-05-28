package com.jobhelper.careerflowapi.ai.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record OpenAiRequest(
        String model,
        List<OpenAiMessage> messages,
        @JsonProperty("max_tokens") int maxTokens,
        @JsonProperty("response_format") ResponseFormat responseFormat
) {
    public record ResponseFormat(String type) {}
}
