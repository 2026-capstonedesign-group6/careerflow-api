package com.jobhelper.careerflowapi.user.oauth2.dto;

public record GoogleUserInfoResponse(
        String sub,    // providerId
        String email,
        String name    // nickname
) {}
