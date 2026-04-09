package com.jobhelper.careerflowapi.auth.oauth2;

import com.jobhelper.careerflowapi.user.domain.enums.Provider;

public interface OAuthClient {

    Provider provider();

    String getAuthorizationUri();

    OAuthUserInfo getUserInfo(String code);
}
