package com.jobhelper.careerflowapi.user.presentation.dto.response;

import com.jobhelper.careerflowapi.user.domain.entity.Award;
import com.jobhelper.careerflowapi.user.domain.entity.Career;
import com.jobhelper.careerflowapi.user.domain.entity.Education;
import com.jobhelper.careerflowapi.user.domain.entity.FamilyMember;
import com.jobhelper.careerflowapi.user.domain.entity.LanguageCertificate;
import com.jobhelper.careerflowapi.user.domain.entity.License;
import com.jobhelper.careerflowapi.user.domain.entity.OtherActivity;
import com.jobhelper.careerflowapi.user.domain.entity.TechStack;
import com.jobhelper.careerflowapi.user.domain.entity.Training;
import com.jobhelper.careerflowapi.user.domain.entity.UserProfile;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public record ProfileResponse(
        String name,
        String phone,
        LocalDate birthDate,
        String address,
        List<EducationResponse> educations,
        List<CareerResponse> careers,
        List<LanguageCertificateResponse> languageCertificates,
        List<TrainingResponse> trainings,
        List<OtherActivityResponse> otherActivities,
        List<AwardResponse> awards,
        List<LicenseResponse> licenses,
        List<FamilyMemberResponse> familyMembers,
        List<TechStackResponse> techStacks,
        MilitaryResponse military,
        StarResponse star
) {
    public static ProfileResponse of(
            UserProfile profile,
            List<Education> educations,
            List<Career> careers,
            List<LanguageCertificate> languageCertificates,
            List<Training> trainings,
            List<OtherActivity> otherActivities,
            List<Award> awards,
            List<License> licenses,
            List<FamilyMember> familyMembers,
            List<TechStack> techStacks
    ) {
        return new ProfileResponse(
                profile == null ? null : profile.getName(),
                profile == null ? null : profile.getPhone(),
                profile == null ? null : profile.getBirthDate(),
                profile == null ? null : profile.getAddress(),
                educations.stream().map(EducationResponse::from).toList(),
                careers.stream().map(CareerResponse::from).toList(),
                languageCertificates.stream().map(LanguageCertificateResponse::from).toList(),
                trainings.stream().map(TrainingResponse::from).toList(),
                otherActivities.stream().map(OtherActivityResponse::from).toList(),
                awards.stream().map(AwardResponse::from).toList(),
                licenses.stream().map(LicenseResponse::from).toList(),
                familyMembers.stream().map(FamilyMemberResponse::from).toList(),
                techStacks.stream().map(TechStackResponse::from).toList(),
                profile == null ? null : MilitaryResponse.from(profile),
                profile == null ? null : StarResponse.from(profile)
        );
    }

    public static ProfileResponse empty() {
        return new ProfileResponse(
                null, null, null, null,
                Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), null, null
        );
    }
}
