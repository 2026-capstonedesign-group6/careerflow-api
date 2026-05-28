package com.jobhelper.careerflowapi.experience.presentation.controller;

import com.jobhelper.careerflowapi.experience.application.ExperienceService;
import com.jobhelper.careerflowapi.experience.docs.ExperienceControllerDocs;
import com.jobhelper.careerflowapi.experience.presentation.dto.request.CreateExperienceRequest;
import com.jobhelper.careerflowapi.experience.presentation.dto.request.UpdateExperienceRequest;
import com.jobhelper.careerflowapi.experience.presentation.dto.response.ExperienceResponse;
import com.jobhelper.careerflowapi.experience.presentation.dto.response.ExperienceSummaryResponse;
import com.jobhelper.careerflowapi.global.domain.dto.CommonResponse;
import com.jobhelper.careerflowapi.global.domain.dto.CursorResponse;
import com.jobhelper.careerflowapi.global.security.SecurityFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/experiences")
@RequiredArgsConstructor
public class ExperienceController implements ExperienceControllerDocs {

    private final ExperienceService experienceService;
    private final SecurityFacade securityFacade;

    @Override
    @PostMapping
    public ResponseEntity<CommonResponse<ExperienceResponse>> create(
            @RequestBody @Valid CreateExperienceRequest request
    ) {
        Long userId = securityFacade.getCurrentUser().userId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.onCreated(experienceService.create(userId, request)));
    }

    @Override
    @GetMapping
    public ResponseEntity<CommonResponse<CursorResponse<ExperienceSummaryResponse>>> getList(
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long userId = securityFacade.getCurrentUser().userId();
        return ResponseEntity.ok(CommonResponse.onSuccess(experienceService.getList(userId, cursor, size)));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<ExperienceResponse>> getOne(@PathVariable Long id) {
        Long userId = securityFacade.getCurrentUser().userId();
        return ResponseEntity.ok(CommonResponse.onSuccess(experienceService.getOne(userId, id)));
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse<ExperienceResponse>> update(
            @PathVariable Long id,
            @RequestBody @Valid UpdateExperienceRequest request
    ) {
        Long userId = securityFacade.getCurrentUser().userId();
        return ResponseEntity.ok(CommonResponse.onSuccess(experienceService.update(userId, id, request)));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<Void>> delete(@PathVariable Long id) {
        Long userId = securityFacade.getCurrentUser().userId();
        experienceService.delete(userId, id);
        return ResponseEntity.ok(CommonResponse.onSuccess());
    }
}
