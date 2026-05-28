package com.jobhelper.careerflowapi.user.application;

import com.jobhelper.careerflowapi.global.domain.ErrorCode;
import com.jobhelper.careerflowapi.global.exception.BusinessException;
import com.jobhelper.careerflowapi.user.domain.entity.User;
import com.jobhelper.careerflowapi.user.domain.entity.UserProfile;
import com.jobhelper.careerflowapi.user.infrastructure.AwardRepository;
import com.jobhelper.careerflowapi.user.infrastructure.CareerRepository;
import com.jobhelper.careerflowapi.user.infrastructure.EducationRepository;
import com.jobhelper.careerflowapi.user.infrastructure.FamilyMemberRepository;
import com.jobhelper.careerflowapi.user.infrastructure.LanguageCertificateRepository;
import com.jobhelper.careerflowapi.user.infrastructure.LicenseRepository;
import com.jobhelper.careerflowapi.user.infrastructure.OtherActivityRepository;
import com.jobhelper.careerflowapi.user.infrastructure.TechStackRepository;
import com.jobhelper.careerflowapi.user.infrastructure.TrainingRepository;
import com.jobhelper.careerflowapi.user.infrastructure.UserProfileRepository;
import com.jobhelper.careerflowapi.user.infrastructure.UserRepository;
import com.jobhelper.careerflowapi.user.presentation.dto.request.UpdateProfileRequest;
import com.jobhelper.careerflowapi.user.presentation.dto.response.ProfileResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private UserProfileRepository userProfileRepository;
    @Mock private EducationRepository educationRepository;
    @Mock private CareerRepository careerRepository;
    @Mock private LanguageCertificateRepository languageCertificateRepository;
    @Mock private TrainingRepository trainingRepository;
    @Mock private OtherActivityRepository otherActivityRepository;
    @Mock private AwardRepository awardRepository;
    @Mock private LicenseRepository licenseRepository;
    @Mock private FamilyMemberRepository familyMemberRepository;
    @Mock private TechStackRepository techStackRepository;

    @InjectMocks
    private UserProfileService userProfileService;

    @Test
    void getProfile_유저가_없으면_USER_NOT_FOUND_예외를_던진다() {
        given(userRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userProfileService.getProfile(1L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    void getProfile_프로필이_없으면_빈_응답을_반환한다() {
        given(userRepository.findById(1L)).willReturn(Optional.of(mock(User.class)));
        given(userProfileRepository.findByUserId(1L)).willReturn(Optional.empty());
        given(educationRepository.findByUserIdOrderByStartDateDesc(1L)).willReturn(List.of());
        given(careerRepository.findByUserIdOrderByStartDateDesc(1L)).willReturn(List.of());
        given(languageCertificateRepository.findByUserIdOrderByExamDateDesc(1L)).willReturn(List.of());
        given(trainingRepository.findByUserIdOrderByStartDateDesc(1L)).willReturn(List.of());
        given(otherActivityRepository.findByUserIdOrderByStartDateDesc(1L)).willReturn(List.of());
        given(awardRepository.findByUserIdOrderByStartDateDesc(1L)).willReturn(List.of());
        given(licenseRepository.findByUserIdOrderByLicenseNameAsc(1L)).willReturn(List.of());
        given(familyMemberRepository.findByUserId(1L)).willReturn(List.of());
        given(techStackRepository.findByUserId(1L)).willReturn(List.of());

        ProfileResponse result = userProfileService.getProfile(1L);

        assertThat(result.name()).isNull();
        assertThat(result.educations()).isEmpty();
        assertThat(result.careers()).isEmpty();
    }

    @Test
    void updateProfile_유저가_없으면_USER_NOT_FOUND_예외를_던진다() {
        given(userRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userProfileService.updateProfile(1L, updateRequest()))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    void updateProfile_기존_데이터를_삭제하고_새로_저장한다() {
        User user = mock(User.class);
        UserProfile profile = mock(UserProfile.class);
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(userProfileRepository.findByUserId(1L)).willReturn(Optional.of(profile));
        given(educationRepository.findByUserIdOrderByStartDateDesc(1L)).willReturn(List.of());
        given(careerRepository.findByUserIdOrderByStartDateDesc(1L)).willReturn(List.of());
        given(languageCertificateRepository.findByUserIdOrderByExamDateDesc(1L)).willReturn(List.of());
        given(trainingRepository.findByUserIdOrderByStartDateDesc(1L)).willReturn(List.of());
        given(otherActivityRepository.findByUserIdOrderByStartDateDesc(1L)).willReturn(List.of());
        given(awardRepository.findByUserIdOrderByStartDateDesc(1L)).willReturn(List.of());
        given(licenseRepository.findByUserIdOrderByLicenseNameAsc(1L)).willReturn(List.of());
        given(familyMemberRepository.findByUserId(1L)).willReturn(List.of());
        given(techStackRepository.findByUserId(1L)).willReturn(List.of());

        userProfileService.updateProfile(1L, updateRequest());

        verify(educationRepository).deleteByUserId(1L);
        verify(careerRepository).deleteByUserId(1L);
        verify(languageCertificateRepository).deleteByUserId(1L);
        verify(trainingRepository).deleteByUserId(1L);
        verify(otherActivityRepository).deleteByUserId(1L);
        verify(awardRepository).deleteByUserId(1L);
        verify(licenseRepository).deleteByUserId(1L);
        verify(familyMemberRepository).deleteByUserId(1L);
        verify(techStackRepository).deleteByUserId(1L);
        verify(profile).update(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    private UpdateProfileRequest updateRequest() {
        return new UpdateProfileRequest(
                "홍길동", "010-1234-5678", null, "서울시 강남구",
                List.of(), List.of(), List.of(), List.of(), List.of(),
                List.of(), List.of(), List.of(), List.of(), null, null
        );
    }
}
