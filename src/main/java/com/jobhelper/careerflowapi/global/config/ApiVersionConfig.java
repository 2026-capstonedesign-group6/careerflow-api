package com.jobhelper.careerflowapi.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ApiVersionConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApiVersionConfig implements WebMvcConfigurer {

    private static final String API_VERSION_HEADER = "X-API-Version";
    private static final String DEFAULT_API_VERSION = "1.0.0";
    private static final String[] SUPPORTED_API_VERSIONS = {
            "1.0.0"
    };

    @Override
    public void configureApiVersioning(ApiVersionConfigurer configurer) {
        configurer
                .useRequestHeader(API_VERSION_HEADER)
                .setDefaultVersion(DEFAULT_API_VERSION)
                .addSupportedVersions(SUPPORTED_API_VERSIONS);
    }
}
