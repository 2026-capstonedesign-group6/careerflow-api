package com.jobhelper.careerflowapi.experience.application;

import com.jobhelper.careerflowapi.experience.domain.entity.Experience;
import com.jobhelper.careerflowapi.experience.infrastructure.ExperienceRepository;
import com.jobhelper.careerflowapi.experience.presentation.dto.request.CreateExperienceRequest;
import com.jobhelper.careerflowapi.experience.presentation.dto.request.UpdateExperienceRequest;
import com.jobhelper.careerflowapi.experience.presentation.dto.response.ExperienceResponse;
import com.jobhelper.careerflowapi.experience.presentation.dto.response.ExperienceSummaryResponse;
import com.jobhelper.careerflowapi.global.domain.ErrorCode;
import com.jobhelper.careerflowapi.global.domain.dto.CursorResponse;
import com.jobhelper.careerflowapi.global.exception.BusinessException;
import com.jobhelper.careerflowapi.user.domain.entity.User;
import com.jobhelper.careerflowapi.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExperienceService {

    private static final int DEFAULT_PAGE_SIZE = 10;

    private final ExperienceRepository experienceRepository;
    private final UserRepository userRepository;

    @Transactional
    public ExperienceResponse create(Long userId, CreateExperienceRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Experience experience = Experience.builder()
                .user(user)
                .title(request.title())
                .situation(request.situation())
                .task(request.task())
                .action(request.action())
                .result(request.result())
                .category(request.category())
                .activityDate(request.activityDate())
                .build();

        return ExperienceResponse.from(experienceRepository.save(experience));
    }

    @Transactional(readOnly = true)
    public CursorResponse<ExperienceSummaryResponse> getList(Long userId, Long cursor, int size) {
        List<Experience> experiences = cursor == null
                ? experienceRepository.findByUserIdOrderByIdDesc(userId, PageRequest.of(0, size + 1))
                : experienceRepository.findByUserIdAndIdLessThanOrderByIdDesc(userId, cursor, PageRequest.of(0, size + 1));

        List<ExperienceSummaryResponse> responses = experiences.stream()
                .map(ExperienceSummaryResponse::from)
                .toList();

        return CursorResponse.of(responses, size);
    }

    @Transactional(readOnly = true)
    public ExperienceResponse getOne(Long userId, Long experienceId) {
        Experience experience = findWithOwnerCheck(userId, experienceId);
        return ExperienceResponse.from(experience);
    }

    @Transactional
    public ExperienceResponse update(Long userId, Long experienceId, UpdateExperienceRequest request) {
        Experience experience = findWithOwnerCheck(userId, experienceId);
        experience.update(request.title(), request.situation(), request.task(),
                request.action(), request.result(), request.category(), request.activityDate());
        return ExperienceResponse.from(experience);
    }

    @Transactional
    public void delete(Long userId, Long experienceId) {
        Experience experience = findWithOwnerCheck(userId, experienceId);
        experienceRepository.delete(experience);
    }

    private Experience findWithOwnerCheck(Long userId, Long experienceId) {
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.EXPERIENCE_NOT_FOUND));

        if (!experience.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.EXPERIENCE_ACCESS_DENIED);
        }
        return experience;
    }
}
