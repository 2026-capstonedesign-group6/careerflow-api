package com.jobhelper.careerflowapi.global.security.dto;

public record AuthUser(
        Long userId,
        String email,
        String role
) {

    public static AuthUser of(Long userId, String Email, String role) {
        return new AuthUser(userId, Email, role);
    }
}
