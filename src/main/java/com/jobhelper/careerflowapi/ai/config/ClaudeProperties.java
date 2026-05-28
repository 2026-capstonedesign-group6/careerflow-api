package com.jobhelper.careerflowapi.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "claude")
public record ClaudeProperties(String apiKey) {}
