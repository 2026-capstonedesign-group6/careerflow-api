package com.jobhelper.careerflowapi.ai.client.dto;

import java.util.List;

public record ClaudeResponse(List<ContentBlock> content) {

    public record ContentBlock(String type, String text) {}

    public String textContent() {
        if (content == null || content.isEmpty()) return "";
        return content.stream()
                .filter(c -> "text".equals(c.type()))
                .findFirst()
                .map(ContentBlock::text)
                .orElse("");
    }
}
