package com.jobhelper.careerflowapi.auth.oauth2;

import com.jobhelper.careerflowapi.auth.oauth2.dto.GoogleTokenResponse;
import com.jobhelper.careerflowapi.auth.oauth2.dto.GoogleUserInfoResponse;
import com.jobhelper.careerflowapi.global.config.OAuthProperties;
import com.jobhelper.careerflowapi.global.domain.ErrorCode;
import com.jobhelper.careerflowapi.global.exception.BusinessException;
import com.jobhelper.careerflowapi.user.domain.enums.Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
@RequiredArgsConstructor
public class GoogleOAuthClient implements OAuthClient {

    private static final String AUTH_BASE_URL = "https://accounts.google.com";

    @Qualifier("googleAuthRestClient")
    private final RestClient googleAuthRestClient;

    @Qualifier("googleApiRestClient")
    private final RestClient googleApiRestClient;

    private final OAuthProperties oAuthProperties;

    @Override
    public Provider provider() {
        return Provider.GOOGLE;
    }

    @Override
    public String getAuthorizationUri() {
        OAuthProperties.ProviderConfig config = oAuthProperties.get("google");
        return AUTH_BASE_URL + "/o/oauth2/v2/auth"
                + "?client_id=" + config.clientId()
                + "&redirect_uri=" + config.redirectUri()
                + "&response_type=code"
                + "&scope=openid%20email%20profile";
    }

    @Override
    public OAuthUserInfo getUserInfo(String code) {
        String accessToken = exchangeToken(code);
        return fetchUserInfo(accessToken);
    }

    private String exchangeToken(String code) {
        OAuthProperties.ProviderConfig config = oAuthProperties.get("google");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", config.clientId());
        params.add("client_secret", config.clientSecret());
        params.add("redirect_uri", config.redirectUri());
        params.add("code", code);

        try {
            GoogleTokenResponse response = googleAuthRestClient.post()
                    .uri("/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(params)
                    .retrieve()
                    .onStatus(status -> !status.is2xxSuccessful(),
                            (req, res) -> { throw new BusinessException(ErrorCode.SOCIAL_LOGIN_FAILED); })
                    .body(GoogleTokenResponse.class);

            if (response == null || response.accessToken() == null) {
                throw new BusinessException(ErrorCode.SOCIAL_LOGIN_FAILED);
            }
            return response.accessToken();
        } catch (RestClientException e) {
            throw new BusinessException(ErrorCode.SOCIAL_LOGIN_FAILED);
        }
    }

    private OAuthUserInfo fetchUserInfo(String accessToken) {
        try {
            GoogleUserInfoResponse response = googleApiRestClient.get()
                    .uri("/oauth2/v3/userinfo")
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .onStatus(status -> !status.is2xxSuccessful(),
                            (req, res) -> { throw new BusinessException(ErrorCode.SOCIAL_LOGIN_FAILED); })
                    .body(GoogleUserInfoResponse.class);

            if (response == null || response.email() == null) {
                throw new BusinessException(ErrorCode.SOCIAL_LOGIN_FAILED);
            }
            return new OAuthUserInfo(response.email(), response.sub(), response.name());
        } catch (RestClientException e) {
            throw new BusinessException(ErrorCode.SOCIAL_LOGIN_FAILED);
        }
    }
}
