package com.jobhelper.careerflowapi.user.oauth2;

public record OAuthUserInfo(
        String email,
        String providerId,
        String nickname
) {}
