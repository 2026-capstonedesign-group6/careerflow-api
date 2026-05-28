package com.jobhelper.careerflowapi.resume.application;

import com.jobhelper.careerflowapi.global.domain.ErrorCode;
import com.jobhelper.careerflowapi.global.domain.dto.CursorResponse;
import com.jobhelper.careerflowapi.global.exception.BusinessException;
import com.jobhelper.careerflowapi.resume.domain.entity.EssayItem;
import com.jobhelper.careerflowapi.resume.domain.entity.Resume;
import com.jobhelper.careerflowapi.resume.infrastructure.ResumeRepository;
import com.jobhelper.careerflowapi.resume.presentation.dto.request.CreateResumeRequest;
import com.jobhelper.careerflowapi.resume.presentation.dto.response.ResumeResponse;
import com.jobhelper.careerflowapi.resume.presentation.dto.response.ResumeSummaryResponse;
import com.jobhelper.careerflowapi.user.domain.entity.User;
import com.jobhelper.careerflowapi.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;

    @Transactional
    public ResumeResponse create(Long userId, CreateResumeRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Resume resume = Resume.builder()
                .user(user)
                .title(request.title())
                .stepProgress(request.stepProgress())
                .build();

        if (request.essays() != null) {
            request.essays().forEach(req -> {
                EssayItem essay = EssayItem.builder()
                        .question(req.question())
                        .content(req.content())
                        .orderIndex(req.orderIndex())
                        .build();
                resume.addEssay(essay);
            });
        }

        return ResumeResponse.from(resumeRepository.save(resume));
    }

    @Transactional(readOnly = true)
    public CursorResponse<ResumeSummaryResponse> getList(Long userId, Long cursor, int size) {
        List<Resume> resumes = cursor == null
                ? resumeRepository.findByUserIdOrderByIdDesc(userId, PageRequest.of(0, size + 1))
                : resumeRepository.findByUserIdAndIdLessThanOrderByIdDesc(userId, cursor, PageRequest.of(0, size + 1));

        List<ResumeSummaryResponse> responses = resumes.stream()
                .map(ResumeSummaryResponse::from)
                .toList();

        return CursorResponse.of(responses, size);
    }

    @Transactional(readOnly = true)
    public ResumeResponse getOne(Long userId, Long resumeId) {
        return ResumeResponse.from(findWithOwnerCheck(userId, resumeId));
    }

    @Transactional
    public void delete(Long userId, Long resumeId) {
        Resume resume = findWithOwnerCheck(userId, resumeId);
        resumeRepository.delete(resume);
    }

    private Resume findWithOwnerCheck(Long userId, Long resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESUME_NOT_FOUND));

        if (!resume.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.RESUME_ACCESS_DENIED);
        }
        return resume;
    }
}
