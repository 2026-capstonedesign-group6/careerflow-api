package com.jobhelper.careerflowapi.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "oauth2")
public record OAuthProperties(Map<String, ProviderConfig> providers) {

    public record ProviderConfig(
            String clientId,
            String clientSecret,
            String redirectUri
    ) {}

    public ProviderConfig get(String provider) {
        ProviderConfig config = providers.get(provider.toLowerCase());
        if (config == null) {
            throw new IllegalArgumentException("지원하지 않는 OAuth2 Provider: " + provider);
        }
        return config;
    }
}
