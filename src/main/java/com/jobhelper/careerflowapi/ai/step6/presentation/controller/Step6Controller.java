package com.jobhelper.careerflowapi.ai.step6.presentation.controller;

import com.jobhelper.careerflowapi.ai.step6.application.Step6CommandHandler;
import com.jobhelper.careerflowapi.ai.step6.docs.Step6ControllerDocs;
import com.jobhelper.careerflowapi.ai.step6.presentation.dto.request.EssayGenerateRequest;
import com.jobhelper.careerflowapi.ai.step6.presentation.dto.response.EssayGenerateResponse;
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
@RequestMapping("/api/step6")
@RequiredArgsConstructor
public class Step6Controller implements Step6ControllerDocs {

    private final Step6CommandHandler step6CommandHandler;
    private final SecurityFacade securityFacade;

    @Override
    @PostMapping("/essay")
    public ResponseEntity<CommonResponse<EssayGenerateResponse>> generateEssay(
            @RequestBody @Valid EssayGenerateRequest request
    ) {
        Long userId = securityFacade.getCurrentUser().userId();
        return ResponseEntity.ok(CommonResponse.onSuccess(step6CommandHandler.generateEssay(request, userId)));
    }
}
