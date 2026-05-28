package com.jobhelper.careerflowapi.user.presentation.dto.response;

import com.jobhelper.careerflowapi.user.domain.entity.UserProfile;

import java.time.LocalDate;

public record MilitaryResponse(
        LocalDate startDate,
        LocalDate endDate,
        String militaryType,
        String exemptionReason
) {
    public static MilitaryResponse from(UserProfile profile) {
        return new MilitaryResponse(
                profile.getMilitaryStartDate(),
                profile.getMilitaryEndDate(),
                profile.getMilitaryType(),
                profile.getMilitaryExemptionReason()
        );
    }
}
