package com.jobhelper.careerflowapi.user.presentation.dto.response;

import com.jobhelper.careerflowapi.user.domain.entity.UserProfile;

public record StarResponse(
        String situation,
        String task,
        String action,
        String result
) {
    public static StarResponse from(UserProfile profile) {
        return new StarResponse(
                profile.getStarSituation(),
                profile.getStarTask(),
                profile.getStarAction(),
                profile.getStarResult()
        );
    }
}
