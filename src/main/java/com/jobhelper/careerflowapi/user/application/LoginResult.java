package com.jobhelper.careerflowapi.user.application;

import org.springframework.http.ResponseCookie;

public record LoginResult(
        String accessToken,
        ResponseCookie refreshCookie
) {
}
