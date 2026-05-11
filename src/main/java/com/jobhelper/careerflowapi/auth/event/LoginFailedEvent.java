package com.jobhelper.careerflowapi.auth.event;

public record LoginFailedEvent(
        String email,
        String reason
) {
}
