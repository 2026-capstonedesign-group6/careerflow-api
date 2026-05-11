package com.jobhelper.careerflowapi.user.domain.entity;

import com.jobhelper.careerflowapi.user.domain.enums.Provider;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserAccountTest {

    @Test
    void builder_로컬_계정을_생성한다() {
        UserAccount account = UserAccount.builder()
                .provider(Provider.LOCAL)
                .password("encoded-pw")
                .build();

        assertThat(account.getProvider()).isEqualTo(Provider.LOCAL);
        assertThat(account.getPassword()).isEqualTo("encoded-pw");
        assertThat(account.getProviderId()).isNull();
    }

    @Test
    void builder_소셜_계정을_생성한다() {
        UserAccount account = UserAccount.builder()
                .provider(Provider.KAKAO)
                .providerId("kakao-user-123")
                .build();

        assertThat(account.getProvider()).isEqualTo(Provider.KAKAO);
        assertThat(account.getProviderId()).isEqualTo("kakao-user-123");
        assertThat(account.getPassword()).isNull();
    }

    @Test
    void changePassword_비밀번호를_변경한다() {
        UserAccount account = UserAccount.builder()
                .provider(Provider.LOCAL)
                .password("old-encoded-pw")
                .build();

        account.changePassword("new-encoded-pw");

        assertThat(account.getPassword()).isEqualTo("new-encoded-pw");
    }
}
