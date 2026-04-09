package com.jobhelper.careerflowapi.user.presentation.controller;

import com.jobhelper.careerflowapi.global.domain.ErrorCode;
import com.jobhelper.careerflowapi.global.domain.dto.CommonResponse;
import com.jobhelper.careerflowapi.global.exception.BusinessException;
import com.jobhelper.careerflowapi.user.application.LoginResult;
import com.jobhelper.careerflowapi.user.command.AccountCommandHandler;
import com.jobhelper.careerflowapi.user.docs.OAuthControllerDocs;
import com.jobhelper.careerflowapi.user.domain.enums.Provider;
import com.jobhelper.careerflowapi.user.presentation.dto.request.OAuthCallbackRequest;
import com.jobhelper.careerflowapi.user.presentation.dto.response.LoginResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth/oauth2")
@RequiredArgsConstructor
public class OAuthController implements OAuthControllerDocs {

    private final AccountCommandHandler commandHandler;

    @Override
    @GetMapping("/authorization/{provider}")
    public void authorize(@PathVariable String provider, HttpServletResponse response) throws IOException {
        Provider resolvedProvider = resolveProvider(provider);
        String authorizationUri = commandHandler.getOAuth2AuthorizationUri(resolvedProvider);
        response.sendRedirect(authorizationUri);
    }

    @Override
    @PostMapping("/callback/{provider}")
    public ResponseEntity<CommonResponse<LoginResponse>> callback(
            @PathVariable String provider,
            @RequestBody @Valid OAuthCallbackRequest request
    ) {
        Provider resolvedProvider = resolveProvider(provider);
        LoginResult result = commandHandler.oAuth2Login(resolvedProvider, request.code());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, result.refreshCookie().toString())
                .body(CommonResponse.onSuccess(new LoginResponse(result.accessToken())));
    }

    private Provider resolveProvider(String provider) {
        try {
            return Provider.valueOf(provider.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.INVALID_PROVIDER);
        }
    }
}
