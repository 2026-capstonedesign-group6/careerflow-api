package com.jobhelper.careerflowapi.ai.step6.docs;

import com.jobhelper.careerflowapi.ai.step6.presentation.dto.request.EssayGenerateRequest;
import com.jobhelper.careerflowapi.ai.step6.presentation.dto.response.EssayGenerateResponse;
import com.jobhelper.careerflowapi.global.docs.CommonCreateErrorDocs;
import com.jobhelper.careerflowapi.global.domain.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Step6", description = "자소서 본문 생성 API")
public interface Step6ControllerDocs {

    @Operation(summary = "선택된 경험 + 문항 → 자소서 본문 생성")
    @CommonCreateErrorDocs
    ResponseEntity<CommonResponse<EssayGenerateResponse>> generateEssay(EssayGenerateRequest request);
}
