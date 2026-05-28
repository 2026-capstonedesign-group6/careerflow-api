package com.jobhelper.careerflowapi.resume.application;

import com.jobhelper.careerflowapi.global.domain.ErrorCode;
import com.jobhelper.careerflowapi.global.exception.BusinessException;
import com.jobhelper.careerflowapi.resume.domain.entity.Resume;
import com.jobhelper.careerflowapi.resume.infrastructure.ResumeRepository;
import com.jobhelper.careerflowapi.resume.presentation.dto.request.CreateResumeRequest;
import com.jobhelper.careerflowapi.user.domain.entity.User;
import com.jobhelper.careerflowapi.user.infrastructure.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ResumeServiceTest {

    @Mock
    private ResumeRepository resumeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ResumeService resumeService;

    @Test
    void create_유저가_없으면_USER_NOT_FOUND_예외를_던진다() {
        given(userRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> resumeService.create(1L, createRequest()))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    void create_이력서를_저장하고_반환한다() {
        User user = mockUser(1L);
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(resumeRepository.save(any(Resume.class))).willAnswer(i -> i.getArgument(0));

        resumeService.create(1L, createRequest());

        verify(resumeRepository).save(any(Resume.class));
    }

    @Test
    void getOne_이력서가_없으면_RESUME_NOT_FOUND_예외를_던진다() {
        given(resumeRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> resumeService.getOne(1L, 99L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.RESUME_NOT_FOUND);
    }

    @Test
    void getOne_다른_유저의_이력서이면_RESUME_ACCESS_DENIED_예외를_던진다() {
        Resume resume = mockResume(1L, 2L);
        given(resumeRepository.findById(1L)).willReturn(Optional.of(resume));

        assertThatThrownBy(() -> resumeService.getOne(99L, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.RESUME_ACCESS_DENIED);
    }

    @Test
    void delete_이력서를_삭제한다() {
        Resume resume = mockResume(1L, 1L);
        given(resumeRepository.findById(1L)).willReturn(Optional.of(resume));

        resumeService.delete(1L, 1L);

        verify(resumeRepository).delete(resume);
    }

    private User mockUser(Long id) {
        User user = mock(User.class);
        given(user.getId()).willReturn(id);
        return user;
    }

    private Resume mockResume(Long resumeId, Long ownerId) {
        User owner = mockUser(ownerId);
        Resume resume = mock(Resume.class);
        given(resume.getId()).willReturn(resumeId);
        given(resume.getUser()).willReturn(owner);
        given(resume.getTitle()).willReturn("네이버 공채 지원");
        given(resume.getStepProgress()).willReturn(3);
        given(resume.getEssays()).willReturn(List.of());
        return resume;
    }

    private CreateResumeRequest createRequest() {
        return new CreateResumeRequest("네이버 공채 지원", 3, List.of());
    }
}
