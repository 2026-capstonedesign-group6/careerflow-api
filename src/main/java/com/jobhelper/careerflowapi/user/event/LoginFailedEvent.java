package com.jobhelper.careerflowapi.user.event;

public record LoginFailedEvent(
        String email,
        String reason
) {
}
