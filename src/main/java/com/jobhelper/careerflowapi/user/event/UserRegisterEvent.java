package com.jobhelper.careerflowapi.user.event;

import com.jobhelper.careerflowapi.user.domain.entity.User;

public record UserRegisterEvent(User user) {
}
