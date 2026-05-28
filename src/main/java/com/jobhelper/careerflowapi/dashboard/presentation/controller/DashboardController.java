package com.jobhelper.careerflowapi.dashboard.presentation.controller;

import com.jobhelper.careerflowapi.dashboard.application.DashboardService;
import com.jobhelper.careerflowapi.dashboard.docs.DashboardControllerDocs;
import com.jobhelper.careerflowapi.dashboard.presentation.dto.response.DashboardResponse;
import com.jobhelper.careerflowapi.global.domain.dto.CommonResponse;
import com.jobhelper.careerflowapi.global.security.SecurityFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController implements DashboardControllerDocs {

    private final DashboardService dashboardService;
    private final SecurityFacade securityFacade;

    @Override
    @GetMapping
    public ResponseEntity<CommonResponse<DashboardResponse>> getDashboard() {
        Long userId = securityFacade.getCurrentUser().userId();
        return ResponseEntity.ok(CommonResponse.onSuccess(dashboardService.getDashboard(userId)));
    }
}
