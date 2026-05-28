package com.jobhelper.careerflowapi.dashboard.docs;

import com.jobhelper.careerflowapi.dashboard.presentation.dto.response.DashboardResponse;
import com.jobhelper.careerflowapi.global.docs.CommonReadErrorDocs;
import com.jobhelper.careerflowapi.global.domain.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Dashboard", description = "대시보드 API")
public interface DashboardControllerDocs {

    @Operation(summary = "대시보드 조회 (프로젝트 현황 + 진행률)")
    @CommonReadErrorDocs
    ResponseEntity<CommonResponse<DashboardResponse>> getDashboard();
}
