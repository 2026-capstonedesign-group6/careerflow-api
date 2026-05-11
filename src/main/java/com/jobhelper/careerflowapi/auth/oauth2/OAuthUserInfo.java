package com.jobhelper.careerflowapi.auth.oauth2;

public record OAuthUserInfo(
        String email,
        String providerId,
        String nickname
) {}
