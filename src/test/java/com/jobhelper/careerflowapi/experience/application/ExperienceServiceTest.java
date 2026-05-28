package com.jobhelper.careerflowapi.experience.application;

import com.jobhelper.careerflowapi.experience.domain.entity.Experience;
import com.jobhelper.careerflowapi.experience.infrastructure.ExperienceRepository;
import com.jobhelper.careerflowapi.experience.presentation.dto.request.CreateExperienceRequest;
import com.jobhelper.careerflowapi.experience.presentation.dto.request.UpdateExperienceRequest;
import com.jobhelper.careerflowapi.global.domain.ErrorCode;
import com.jobhelper.careerflowapi.global.exception.BusinessException;
import com.jobhelper.careerflowapi.user.domain.entity.User;
import com.jobhelper.careerflowapi.user.infrastructure.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ExperienceServiceTest {

    @Mock
    private ExperienceRepository experienceRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ExperienceService experienceService;

    @Test
    void create_유저가_없으면_USER_NOT_FOUND_예외를_던진다() {
        given(userRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> experienceService.create(1L, createRequest()))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    void create_유저가_있으면_경험을_저장하고_반환한다() {
        User user = mockUser(1L);
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(experienceRepository.save(any(Experience.class))).willAnswer(i -> i.getArgument(0));

        experienceService.create(1L, createRequest());

        verify(experienceRepository).save(any(Experience.class));
    }

    @Test
    void getOne_경험이_없으면_EXPERIENCE_NOT_FOUND_예외를_던진다() {
        given(experienceRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> experienceService.getOne(1L, 99L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.EXPERIENCE_NOT_FOUND);
    }

    @Test
    void getOne_다른_유저의_경험이면_EXPERIENCE_ACCESS_DENIED_예외를_던진다() {
        Experience exp = mockExperience(1L, 2L);
        given(experienceRepository.findById(1L)).willReturn(Optional.of(exp));

        assertThatThrownBy(() -> experienceService.getOne(99L, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.EXPERIENCE_ACCESS_DENIED);
    }

    @Test
    void getOne_소유자이면_예외_없이_반환한다() {
        Experience exp = mockExperience(1L, 1L);
        given(experienceRepository.findById(1L)).willReturn(Optional.of(exp));

        assertThatCode(() -> experienceService.getOne(1L, 1L)).doesNotThrowAnyException();
    }

    @Test
    void update_소유자이면_경험을_수정한다() {
        Experience exp = mockExperience(1L, 1L);
        given(experienceRepository.findById(1L)).willReturn(Optional.of(exp));

        experienceService.update(1L, 1L, updateRequest());

        verify(exp).update(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void delete_소유자이면_경험을_삭제한다() {
        Experience exp = mockExperience(1L, 1L);
        given(experienceRepository.findById(1L)).willReturn(Optional.of(exp));

        experienceService.delete(1L, 1L);

        verify(experienceRepository).delete(exp);
    }

    private User mockUser(Long id) {
        User user = mock(User.class);
        given(user.getId()).willReturn(id);
        return user;
    }

    private Experience mockExperience(Long experienceId, Long ownerId) {
        User owner = mockUser(ownerId);
        Experience exp = mock(Experience.class);
        given(exp.getId()).willReturn(experienceId);
        given(exp.getUser()).willReturn(owner);
        given(exp.getTitle()).willReturn("캡스톤 팀 리더");
        given(exp.getCategory()).willReturn("팀플");
        given(exp.getActivityDate()).willReturn(LocalDate.of(2024, 6, 1));
        return exp;
    }

    private CreateExperienceRequest createRequest() {
        return new CreateExperienceRequest("캡스톤 팀 리더", "상황", "과제", "행동", "결과", "팀플",
                LocalDate.of(2024, 6, 1));
    }

    private UpdateExperienceRequest updateRequest() {
        return new UpdateExperienceRequest("수정된 제목", "상황2", "과제2", "행동2", "결과2", "인턴십",
                LocalDate.of(2024, 7, 1));
    }
}
