package com.jobhelper.careerflowapi.dashboard.presentation.dto.response;

import com.jobhelper.careerflowapi.resume.presentation.dto.response.ResumeSummaryResponse;

import java.util.List;

public record DashboardResponse(
        long resumeCount,
        long experienceCount,
        int profileCompletionRate,
        List<ResumeSummaryResponse> recentResumes
) {
}
