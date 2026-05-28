package com.jobhelper.careerflowapi.ai.client.dto;

import java.util.List;

public record OpenAiResponse(List<Choice> choices) {

    public record Choice(Message message) {}

    public record Message(String role, String content) {}

    public String textContent() {
        if (choices == null || choices.isEmpty()) return "";
        Message msg = choices.get(0).message();
        return msg != null && msg.content() != null ? msg.content() : "";
    }
}
