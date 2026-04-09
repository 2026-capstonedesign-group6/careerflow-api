package com.jobhelper.careerflowapi.user.application.dto;

import org.springframework.http.ResponseCookie;

public record LoginResult(
        String accessToken,
        ResponseCookie refreshCookie
) {
}
