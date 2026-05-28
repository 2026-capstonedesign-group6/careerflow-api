package com.jobhelper.careerflowapi.user.application;

import com.jobhelper.careerflowapi.global.domain.ErrorCode;
import com.jobhelper.careerflowapi.global.exception.BusinessException;
import com.jobhelper.careerflowapi.user.domain.entity.Award;
import com.jobhelper.careerflowapi.user.domain.entity.Career;
import com.jobhelper.careerflowapi.user.domain.entity.Education;
import com.jobhelper.careerflowapi.user.domain.entity.FamilyMember;
import com.jobhelper.careerflowapi.user.domain.entity.LanguageCertificate;
import com.jobhelper.careerflowapi.user.domain.entity.License;
import com.jobhelper.careerflowapi.user.domain.entity.OtherActivity;
import com.jobhelper.careerflowapi.user.domain.entity.TechStack;
import com.jobhelper.careerflowapi.user.domain.entity.Training;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final EducationRepository educationRepository;
    private final CareerRepository careerRepository;
    private final LanguageCertificateRepository languageCertificateRepository;
    private final TrainingRepository trainingRepository;
    private final OtherActivityRepository otherActivityRepository;
    private final AwardRepository awardRepository;
    private final LicenseRepository licenseRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final TechStackRepository techStackRepository;

    @Transactional(readOnly = true)
    public ProfileResponse getProfile(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        UserProfile profile = userProfileRepository.findByUserId(userId).orElse(null);
        List<Education> educations = educationRepository.findByUserIdOrderByStartDateDesc(userId);
        List<Career> careers = careerRepository.findByUserIdOrderByStartDateDesc(userId);
        List<LanguageCertificate> languageCertificates = languageCertificateRepository.findByUserIdOrderByExamDateDesc(userId);
        List<Training> trainings = trainingRepository.findByUserIdOrderByStartDateDesc(userId);
        List<OtherActivity> otherActivities = otherActivityRepository.findByUserIdOrderByStartDateDesc(userId);
        List<Award> awards = awardRepository.findByUserIdOrderByStartDateDesc(userId);
        List<License> licenses = licenseRepository.findByUserIdOrderByLicenseNameAsc(userId);
        List<FamilyMember> familyMembers = familyMemberRepository.findByUserId(userId);
        List<TechStack> techStacks = techStackRepository.findByUserId(userId);

        return ProfileResponse.of(profile, educations, careers, languageCertificates, trainings,
                otherActivities, awards, licenses, familyMembers, techStacks);
    }

    @Transactional
    public ProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseGet(() -> userProfileRepository.save(UserProfile.builder().user(user).build()));

        LocalDate militaryStartDate = request.military() != null ? request.military().startDate() : null;
        LocalDate militaryEndDate = request.military() != null ? request.military().endDate() : null;
        String militaryType = request.military() != null ? request.military().militaryType() : null;
        String exemptionReason = request.military() != null ? request.military().exemptionReason() : null;

        String starSituation = request.star() != null ? request.star().situation() : null;
        String starTask = request.star() != null ? request.star().task() : null;
        String starAction = request.star() != null ? request.star().action() : null;
        String starResult = request.star() != null ? request.star().result() : null;

        profile.update(request.name(), request.phone(), request.birthDate(), request.address(),
                militaryStartDate, militaryEndDate, militaryType, exemptionReason,
                starSituation, starTask, starAction, starResult);

        educationRepository.deleteByUserId(userId);
        careerRepository.deleteByUserId(userId);
        languageCertificateRepository.deleteByUserId(userId);
        trainingRepository.deleteByUserId(userId);
        otherActivityRepository.deleteByUserId(userId);
        awardRepository.deleteByUserId(userId);
        licenseRepository.deleteByUserId(userId);
        familyMemberRepository.deleteByUserId(userId);
        techStackRepository.deleteByUserId(userId);

        if (request.educations() != null) {
            request.educations().forEach(req -> educationRepository.save(
                    Education.builder()
                            .user(user).schoolName(req.schoolName()).major(req.major())
                            .level(req.level()).startDate(req.startDate()).endDate(req.endDate())
                            .isAttending(req.isAttending()).build()
            ));
        }

        if (request.careers() != null) {
            request.careers().forEach(req -> careerRepository.save(
                    Career.builder()
                            .user(user).companyName(req.companyName()).position(req.position())
                            .description(req.description()).startDate(req.startDate())
                            .endDate(req.endDate()).isCurrent(req.isCurrent()).build()
            ));
        }

        if (request.languageCertificates() != null) {
            request.languageCertificates().forEach(req -> languageCertificateRepository.save(
                    LanguageCertificate.builder()
                            .user(user).language(req.language()).examName(req.examName())
                            .score(req.score()).examDate(req.examDate()).build()
            ));
        }

        if (request.trainings() != null) {
            request.trainings().forEach(req -> trainingRepository.save(
                    Training.builder()
                            .user(user).courseName(req.courseName()).instituteName(req.instituteName())
                            .startDate(req.startDate()).endDate(req.endDate()).build()
            ));
        }

        if (request.otherActivities() != null) {
            request.otherActivities().forEach(req -> otherActivityRepository.save(
                    OtherActivity.builder()
                            .user(user).content(req.content()).organizationName(req.organizationName())
                            .startDate(req.startDate()).endDate(req.endDate()).build()
            ));
        }

        if (request.awards() != null) {
            request.awards().forEach(req -> awardRepository.save(
                    Award.builder()
                            .user(user).awardTitle(req.awardTitle()).organizationName(req.organizationName())
                            .startDate(req.startDate()).endDate(req.endDate()).build()
            ));
        }

        if (request.licenses() != null) {
            request.licenses().forEach(req -> licenseRepository.save(
                    License.builder()
                            .user(user).licenseName(req.licenseName()).licenseNumber(req.licenseNumber())
                            .grade(req.grade()).issuingOrganization(req.issuingOrganization()).build()
            ));
        }

        if (request.familyMembers() != null) {
            request.familyMembers().forEach(req -> familyMemberRepository.save(
                    FamilyMember.builder()
                            .user(user).relation(req.relation()).name(req.name())
                            .birthYear(req.birthYear()).occupation(req.occupation()).build()
            ));
        }

        if (request.techStacks() != null) {
            request.techStacks().forEach(req -> techStackRepository.save(
                    TechStack.builder().user(user).skill(req.skill()).build()
            ));
        }

        List<Education> educations = educationRepository.findByUserIdOrderByStartDateDesc(userId);
        List<Career> careers = careerRepository.findByUserIdOrderByStartDateDesc(userId);
        List<LanguageCertificate> languageCertificates = languageCertificateRepository.findByUserIdOrderByExamDateDesc(userId);
        List<Training> trainings = trainingRepository.findByUserIdOrderByStartDateDesc(userId);
        List<OtherActivity> otherActivities = otherActivityRepository.findByUserIdOrderByStartDateDesc(userId);
        List<Award> awards = awardRepository.findByUserIdOrderByStartDateDesc(userId);
        List<License> licenses = licenseRepository.findByUserIdOrderByLicenseNameAsc(userId);
        List<FamilyMember> familyMembers = familyMemberRepository.findByUserId(userId);
        List<TechStack> techStacks = techStackRepository.findByUserId(userId);

        return ProfileResponse.of(profile, educations, careers, languageCertificates, trainings,
                otherActivities, awards, licenses, familyMembers, techStacks);
    }
}
