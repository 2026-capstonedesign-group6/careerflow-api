package com.jobhelper.careerflowapi.auth.event;

import com.jobhelper.careerflowapi.user.domain.enums.Provider;

public record LinkFactorEvent(
        Long userId,
        Provider provider
) {
}
