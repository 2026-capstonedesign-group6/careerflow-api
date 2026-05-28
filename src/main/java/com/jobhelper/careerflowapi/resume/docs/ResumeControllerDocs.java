package com.jobhelper.careerflowapi.resume.docs;

import com.jobhelper.careerflowapi.global.docs.CommonCreateErrorDocs;
import com.jobhelper.careerflowapi.global.docs.CommonReadErrorDocs;
import com.jobhelper.careerflowapi.global.domain.dto.CommonResponse;
import com.jobhelper.careerflowapi.global.domain.dto.CursorResponse;
import com.jobhelper.careerflowapi.resume.presentation.dto.request.CreateResumeRequest;
import com.jobhelper.careerflowapi.resume.presentation.dto.response.ResumeResponse;
import com.jobhelper.careerflowapi.resume.presentation.dto.response.ResumeSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Resume", description = "이력서/자소서 API")
public interface ResumeControllerDocs {

    @Operation(summary = "이력서 생성")
    @CommonCreateErrorDocs
    ResponseEntity<CommonResponse<ResumeResponse>> create(@RequestBody @Valid CreateResumeRequest request);

    @Operation(summary = "이력서 목록 조회 (커서 페이지네이션)")
    @CommonReadErrorDocs
    ResponseEntity<CommonResponse<CursorResponse<ResumeSummaryResponse>>> getList(
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") int size
    );

    @Operation(summary = "이력서 단건 조회")
    @CommonReadErrorDocs
    ResponseEntity<CommonResponse<ResumeResponse>> getOne(@PathVariable Long id);

    @Operation(summary = "이력서 삭제")
    @CommonReadErrorDocs
    ResponseEntity<CommonResponse<Void>> delete(@PathVariable Long id);
}
