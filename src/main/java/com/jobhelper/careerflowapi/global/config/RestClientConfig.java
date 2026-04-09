package com.jobhelper.careerflowapi.global.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class RestClientConfig {

    /* 사용 예시

    // config
    @Bean("kakaoRestClient")
    public RestClient kakaoRestClient() {
        return RestClient.builder()
                .baseUrl("https://kapi.kakao.com")
                .build();
    }

    // service
    public KakaoUserInfo getKakaoUserInfo(String accessToken) {
        return restClient.get()
                .uri("/v2/user/me")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(KakaoUserInfo.class);
    }

    // config
    @Bean("tossRestClient")
    public RestClient tossRestClient() {
        return RestClient.builder()
                .baseUrl("https://api.tosspayments.com")
                .defaultHeader("Authorization", "Basic " + encodedSecretKey)
                .build();
    }

    // service
    public PaymentResponse confirmPayment(PaymentRequest request) {
        return restClient.post()
                .uri("/v1/payments/confirm")
                .body(request)
                .retrieve()
                .body(PaymentResponse.class);
    }
     */
}
