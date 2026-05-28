package com.jobhelper.careerflowapi.resume.presentation.controller;

import com.jobhelper.careerflowapi.global.domain.dto.CommonResponse;
import com.jobhelper.careerflowapi.global.domain.dto.CursorResponse;
import com.jobhelper.careerflowapi.global.security.SecurityFacade;
import com.jobhelper.careerflowapi.resume.application.ResumeService;
import com.jobhelper.careerflowapi.resume.docs.ResumeControllerDocs;
import com.jobhelper.careerflowapi.resume.presentation.dto.request.CreateResumeRequest;
import com.jobhelper.careerflowapi.resume.presentation.dto.response.ResumeResponse;
import com.jobhelper.careerflowapi.resume.presentation.dto.response.ResumeSummaryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController implements ResumeControllerDocs {

    private final ResumeService resumeService;
    private final SecurityFacade securityFacade;

    @Override
    @PostMapping
    public ResponseEntity<CommonResponse<ResumeResponse>> create(
            @RequestBody @Valid CreateResumeRequest request
    ) {
        Long userId = securityFacade.getCurrentUser().userId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.onCreated(resumeService.create(userId, request)));
    }

    @Override
    @GetMapping
    public ResponseEntity<CommonResponse<CursorResponse<ResumeSummaryResponse>>> getList(
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long userId = securityFacade.getCurrentUser().userId();
        return ResponseEntity.ok(CommonResponse.onSuccess(resumeService.getList(userId, cursor, size)));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<ResumeResponse>> getOne(@PathVariable Long id) {
        Long userId = securityFacade.getCurrentUser().userId();
        return ResponseEntity.ok(CommonResponse.onSuccess(resumeService.getOne(userId, id)));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<Void>> delete(@PathVariable Long id) {
        Long userId = securityFacade.getCurrentUser().userId();
        resumeService.delete(userId, id);
        return ResponseEntity.ok(CommonResponse.onSuccess());
    }
}
