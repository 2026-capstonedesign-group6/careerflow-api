package com.jobhelper.careerflowapi.user.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;

public record UpdateProfileRequest(
        @Schema(example = "홍길동")
        String name,

        @Schema(example = "010-1234-5678")
        String phone,

        @Schema(example = "1999-01-01")
        LocalDate birthDate,

        @Schema(example = "서울시 강남구")
        String address,

        @Valid List<EducationRequest> educations,
        @Valid List<CareerRequest> careers,
        @Valid List<LanguageCertificateRequest> languageCertificates,
        @Valid List<TrainingRequest> trainings,
        @Valid List<OtherActivityRequest> otherActivities,
        @Valid List<AwardRequest> awards,
        @Valid List<LicenseRequest> licenses,
        @Valid List<FamilyMemberRequest> familyMembers,
        @Valid List<TechStackRequest> techStacks,
        @Valid MilitaryRequest military,
        @Valid StarRequest star
) {}
