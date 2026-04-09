package com.jobhelper.careerflowapi.user.event;

import com.jobhelper.careerflowapi.user.application.LoginResult;
import com.jobhelper.careerflowapi.user.domain.entity.User;
import lombok.Getter;

@Getter
public class LoginEvent {

    private final User user;
    private LoginResult result;

    public LoginEvent(User user) {
        this.user = user;
    }

    public void complete(LoginResult result) {
        this.result = result;
    }

    public LoginResult getResult() {
        if (result == null) {
            throw new IllegalStateException("LoginEvent가 완료되지 않았습니다. AuthEventHandler 등록을 확인해주세요.");
        }
        return result;
    }
}
