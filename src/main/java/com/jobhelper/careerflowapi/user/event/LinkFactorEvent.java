package com.jobhelper.careerflowapi.user.event;

import com.jobhelper.careerflowapi.user.domain.enums.Provider;

public record LinkFactorEvent(
        Long userId,
        Provider provider
) {
}
