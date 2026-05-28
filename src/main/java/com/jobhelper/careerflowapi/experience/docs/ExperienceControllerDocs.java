package com.jobhelper.careerflowapi.experience.docs;

import com.jobhelper.careerflowapi.global.docs.CommonCreateErrorDocs;
import com.jobhelper.careerflowapi.global.docs.CommonReadErrorDocs;
import com.jobhelper.careerflowapi.global.domain.dto.CommonResponse;
import com.jobhelper.careerflowapi.global.domain.dto.CursorResponse;
import com.jobhelper.careerflowapi.experience.presentation.dto.request.CreateExperienceRequest;
import com.jobhelper.careerflowapi.experience.presentation.dto.request.UpdateExperienceRequest;
import com.jobhelper.careerflowapi.experience.presentation.dto.response.ExperienceResponse;
import com.jobhelper.careerflowapi.experience.presentation.dto.response.ExperienceSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Experience", description = "경험(STAR) CRUD API")
public interface ExperienceControllerDocs {

    @Operation(summary = "경험 등록")
    @CommonCreateErrorDocs
    ResponseEntity<CommonResponse<ExperienceResponse>> create(@RequestBody @Valid CreateExperienceRequest request);

    @Operation(summary = "경험 목록 조회 (커서 페이지네이션)")
    @CommonReadErrorDocs
    ResponseEntity<CommonResponse<CursorResponse<ExperienceSummaryResponse>>> getList(
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") int size
    );

    @Operation(summary = "경험 단건 조회")
    @CommonReadErrorDocs
    ResponseEntity<CommonResponse<ExperienceResponse>> getOne(@PathVariable Long id);

    @Operation(summary = "경험 수정")
    @CommonReadErrorDocs
    ResponseEntity<CommonResponse<ExperienceResponse>> update(
            @PathVariable Long id,
            @RequestBody @Valid UpdateExperienceRequest request
    );

    @Operation(summary = "경험 삭제")
    @CommonReadErrorDocs
    ResponseEntity<CommonResponse<Void>> delete(@PathVariable Long id);
}
