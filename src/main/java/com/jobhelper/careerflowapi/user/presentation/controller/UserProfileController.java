package com.jobhelper.careerflowapi.user.presentation.controller;

import com.jobhelper.careerflowapi.global.domain.dto.CommonResponse;
import com.jobhelper.careerflowapi.global.security.SecurityFacade;
import com.jobhelper.careerflowapi.user.application.UserProfileService;
import com.jobhelper.careerflowapi.user.docs.UserProfileControllerDocs;
import com.jobhelper.careerflowapi.user.presentation.dto.request.UpdateProfileRequest;
import com.jobhelper.careerflowapi.user.presentation.dto.response.ProfileResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserProfileController implements UserProfileControllerDocs {

    private final UserProfileService userProfileService;
    private final SecurityFacade securityFacade;

    @Override
    @GetMapping("/profile")
    public ResponseEntity<CommonResponse<ProfileResponse>> getProfile() {
        Long userId = securityFacade.getCurrentUser().userId();
        return ResponseEntity.ok(CommonResponse.onSuccess(userProfileService.getProfile(userId)));
    }

    @Override
    @PutMapping("/profile")
    public ResponseEntity<CommonResponse<ProfileResponse>> updateProfile(
            @RequestBody @Valid UpdateProfileRequest request
    ) {
        Long userId = securityFacade.getCurrentUser().userId();
        return ResponseEntity.ok(CommonResponse.onSuccess(userProfileService.updateProfile(userId, request)));
    }
}
