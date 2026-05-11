package com.jobhelper.careerflowapi.auth.oauth2.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoUserInfoResponse(
        Long id,
        @JsonProperty("kakao_account") KakaoAccount kakaoAccount
) {
    public record KakaoAccount(
            String email,
            KakaoProfile profile
    ) {
        public record KakaoProfile(String nickname) {}
    }

    public String email() {
        return kakaoAccount != null ? kakaoAccount.email() : null;
    }

    public String nickname() {
        if (kakaoAccount == null || kakaoAccount.profile() == null) return null;
        return kakaoAccount.profile().nickname();
    }

    public String providerId() {
        return String.valueOf(id);
    }
}
