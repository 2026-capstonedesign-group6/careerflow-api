package com.jobhelper.careerflowapi.user.domain.session;

import lombok.Getter;

@Getter
public class AuthSession {

    private final String email;
    private String refreshToken;

    private AuthSession(String email, String refreshToken) {
        this.email = email;
        this.refreshToken = refreshToken;
    }

    public static AuthSession of(String email, String refreshToken) {
        return new AuthSession(email, refreshToken);
    }

    public boolean isTheft(String incomingToken) {
        return !this.refreshToken.equals(incomingToken);
    }

    public void rotate(String newRefreshToken) {
        this.refreshToken = newRefreshToken;
    }
}
