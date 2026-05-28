package com.jobhelper.careerflowapi.ai.step1.presentation.controller;

import com.jobhelper.careerflowapi.ai.step1.application.Step1CommandHandler;
import com.jobhelper.careerflowapi.ai.step1.docs.Step1ControllerDocs;
import com.jobhelper.careerflowapi.ai.step1.presentation.dto.response.GroupingResponse;
import com.jobhelper.careerflowapi.global.domain.dto.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/step1")
@RequiredArgsConstructor
public class Step1Controller implements Step1ControllerDocs {

    private final Step1CommandHandler step1CommandHandler;

    @Override
    @PostMapping(value = "/group", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonResponse<GroupingResponse>> group(
            @RequestPart(required = false) String rawText,
            @RequestPart(required = false) List<MultipartFile> files
    ) {
        return ResponseEntity.ok(CommonResponse.onSuccess(step1CommandHandler.group(rawText, files)));
    }
}
