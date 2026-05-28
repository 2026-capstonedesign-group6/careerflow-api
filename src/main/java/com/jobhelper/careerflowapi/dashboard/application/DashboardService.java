package com.jobhelper.careerflowapi.dashboard.application;

import com.jobhelper.careerflowapi.dashboard.presentation.dto.response.DashboardResponse;
import com.jobhelper.careerflowapi.experience.infrastructure.ExperienceRepository;
import com.jobhelper.careerflowapi.resume.infrastructure.ResumeRepository;
import com.jobhelper.careerflowapi.resume.presentation.dto.response.ResumeSummaryResponse;
import com.jobhelper.careerflowapi.user.infrastructure.CareerRepository;
import com.jobhelper.careerflowapi.user.infrastructure.EducationRepository;
import com.jobhelper.careerflowapi.user.infrastructure.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final int RECENT_RESUME_SIZE = 5;

    private final ResumeRepository resumeRepository;
    private final ExperienceRepository experienceRepository;
    private final UserProfileRepository userProfileRepository;
    private final EducationRepository educationRepository;
    private final CareerRepository careerRepository;

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(Long userId) {
        long resumeCount = resumeRepository.countByUserId(userId);
        long experienceCount = experienceRepository.countByUserId(userId);

        int profileCompletionRate = calculateCompletionRate(userId, experienceCount);

        List<ResumeSummaryResponse> recentResumes = resumeRepository
                .findByUserIdOrderByIdDesc(userId, PageRequest.of(0, RECENT_RESUME_SIZE))
                .stream()
                .map(ResumeSummaryResponse::from)
                .toList();

        return new DashboardResponse(resumeCount, experienceCount, profileCompletionRate, recentResumes);
    }

    private int calculateCompletionRate(Long userId, long experienceCount) {
        int score = 0;

        boolean hasProfile = userProfileRepository.findByUserId(userId)
                .map(p -> p.getName() != null)
                .orElse(false);
        if (hasProfile) score += 25;
        if (!educationRepository.findByUserIdOrderByStartDateDesc(userId).isEmpty()) score += 25;
        if (!careerRepository.findByUserIdOrderByStartDateDesc(userId).isEmpty()) score += 25;
        if (experienceCount > 0) score += 25;

        return score;
    }
}
