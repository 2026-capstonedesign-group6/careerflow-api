package com.jobhelper.careerflowapi.auth.application.dto;

import org.springframework.http.ResponseCookie;

public record LoginResult(
        String accessToken,
        ResponseCookie refreshCookie
) {
}