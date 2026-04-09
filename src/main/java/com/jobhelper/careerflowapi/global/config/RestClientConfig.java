package com.jobhelper.careerflowapi.global.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(OAuthProperties.class)
public class RestClientConfig {

    @Bean("kakaoAuthRestClient")
    public RestClient kakaoAuthRestClient() {
        return RestClient.builder()
                .baseUrl("https://kauth.kakao.com")
                .build();
    }

    @Bean("kakaoApiRestClient")
    public RestClient kakaoApiRestClient() {
        return RestClient.builder()
                .baseUrl("https://kapi.kakao.com")
                .build();
    }

    @Bean("googleAuthRestClient")
    public RestClient googleAuthRestClient() {
        return RestClient.builder()
                .baseUrl("https://oauth2.googleapis.com")
                .build();
    }

    @Bean("googleApiRestClient")
    public RestClient googleApiRestClient() {
        return RestClient.builder()
                .baseUrl("https://www.googleapis.com")
                .build();
    }

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
