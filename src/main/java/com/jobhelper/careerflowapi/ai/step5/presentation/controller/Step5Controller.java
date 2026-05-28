package com.jobhelper.careerflowapi.ai.step5.presentation.controller;

import com.jobhelper.careerflowapi.ai.step5.application.Step5CommandHandler;
import com.jobhelper.careerflowapi.ai.step5.docs.Step5ControllerDocs;
import com.jobhelper.careerflowapi.ai.step5.presentation.dto.request.RecommendRequest;
import com.jobhelper.careerflowapi.ai.step5.presentation.dto.response.RecommendResponse;
import com.jobhelper.careerflowapi.global.domain.dto.CommonResponse;
import com.jobhelper.careerflowapi.global.security.SecurityFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/step5")
@RequiredArgsConstructor
public class Step5Controller implements Step5ControllerDocs {

    private final Step5CommandHandler step5CommandHandler;
    private final SecurityFacade securityFacade;

    @Override
    @PostMapping("/recommend")
    public ResponseEntity<CommonResponse<RecommendResponse>> recommend(
            @RequestBody @Valid RecommendRequest request
    ) {
        Long userId = securityFacade.getCurrentUser().userId();
        return ResponseEntity.ok(CommonResponse.onSuccess(step5CommandHandler.recommend(request, userId)));
    }
}
