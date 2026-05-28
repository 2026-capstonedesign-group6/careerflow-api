package com.jobhelper.careerflowapi.ai.step5.docs;

import com.jobhelper.careerflowapi.ai.step5.presentation.dto.request.RecommendRequest;
import com.jobhelper.careerflowapi.ai.step5.presentation.dto.response.RecommendResponse;
import com.jobhelper.careerflowapi.global.docs.CommonCreateErrorDocs;
import com.jobhelper.careerflowapi.global.domain.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Step5", description = "문항별 활동 추천 API")
public interface Step5ControllerDocs {

    @Operation(summary = "자소서 문항 + 경험 목록 → 최적 활동 추천")
    @CommonCreateErrorDocs
    ResponseEntity<CommonResponse<RecommendResponse>> recommend(RecommendRequest request);
}
