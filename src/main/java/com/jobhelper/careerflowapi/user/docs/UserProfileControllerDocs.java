package com.jobhelper.careerflowapi.user.docs;

import com.jobhelper.careerflowapi.global.docs.CommonReadErrorDocs;
import com.jobhelper.careerflowapi.global.domain.dto.CommonResponse;
import com.jobhelper.careerflowapi.user.presentation.dto.request.UpdateProfileRequest;
import com.jobhelper.careerflowapi.user.presentation.dto.response.ProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Profile", description = "프로필 API")
public interface UserProfileControllerDocs {

    @Operation(summary = "내 프로필 조회")
    @CommonReadErrorDocs
    ResponseEntity<CommonResponse<ProfileResponse>> getProfile();

    @Operation(summary = "내 프로필 수정")
    @CommonReadErrorDocs
    ResponseEntity<CommonResponse<ProfileResponse>> updateProfile(@RequestBody @Valid UpdateProfileRequest request);
}
