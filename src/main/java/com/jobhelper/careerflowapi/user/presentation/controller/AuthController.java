package com.jobhelper.careerflowapi.user.presentation.controller;

import com.jobhelper.careerflowapi.global.domain.ErrorCode;
import com.jobhelper.careerflowapi.global.domain.dto.CommonResponse;
import com.jobhelper.careerflowapi.global.exception.AuthorizationException;
import com.jobhelper.careerflowapi.user.application.AuthSessionService;
import com.jobhelper.careerflowapi.user.application.LoginResult;
import com.jobhelper.careerflowapi.user.command.AccountCommandHandler;
import com.jobhelper.careerflowapi.user.docs.AuthControllerDocs;
import com.jobhelper.careerflowapi.user.presentation.dto.request.LocalLoginRequest;
import com.jobhelper.careerflowapi.user.presentation.dto.request.LocalSignupRequest;
import com.jobhelper.careerflowapi.user.presentation.dto.response.LoginResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthControllerDocs {

    private final AccountCommandHandler commandHandler;
    private final AuthSessionService authSessionService;

    @Override
    @PostMapping("/signup/local")
    public ResponseEntity<CommonResponse<Void>> localSignup(@RequestBody @Valid LocalSignupRequest request) {
        commandHandler.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.onCreated(null));
    }

    @Override
    @PostMapping("/login/local")
    public ResponseEntity<CommonResponse<LoginResponse>> localLogin(@RequestBody @Valid LocalLoginRequest request) {
        LoginResult result = commandHandler.login(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, result.refreshCookie().toString())
                .body(CommonResponse.onSuccess(new LoginResponse(result.accessToken())));
    }

    @Override
    @PostMapping("/refresh")
    public ResponseEntity<CommonResponse<LoginResponse>> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ) {
        if (refreshToken == null) {
            throw new AuthorizationException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
        LoginResult result = authSessionService.refresh(refreshToken);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, result.refreshCookie().toString())
                .body(CommonResponse.onSuccess(new LoginResponse(result.accessToken())));
    }

    @Override
    @PostMapping("/logout")
    public ResponseEntity<CommonResponse<Void>> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ) {
        if (refreshToken == null) {
            return ResponseEntity.ok().body(CommonResponse.onSuccess());
        }
        ResponseCookie deleteCookie = authSessionService.logout(refreshToken);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body(CommonResponse.onSuccess());
    }
}
