package com.jobhelper.careerflowapi.auth.oauth2.dto;

public record GoogleUserInfoResponse(
        String sub,    // providerId
        String email,
        String name    // nickname
) {}
