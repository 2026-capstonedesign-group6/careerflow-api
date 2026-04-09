package com.jobhelper.careerflowapi.user.domain.entity;

import com.jobhelper.careerflowapi.user.domain.enums.Provider;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void builder_사용자를_생성한다() {
        User user = User.builder()
                .email("user@test.com")
                .nickname("테스터")
                .role("ROLE_USER")
                .build();

        assertThat(user.getEmail()).isEqualTo("user@test.com");
        assertThat(user.getNickname()).isEqualTo("테스터");
        assertThat(user.getRole()).isEqualTo("ROLE_USER");
        assertThat(user.getAccounts()).isEmpty();
    }

    @Test
    void addAccount_계정을_추가하고_양방향_관계를_설정한다() {
        User user = User.builder()
                .email("user@test.com")
                .nickname("테스터")
                .role("ROLE_USER")
                .build();

        UserAccount account = UserAccount.builder()
                .provider(Provider.LOCAL)
                .password("encoded-pw")
                .build();

        user.addAccount(account);

        assertThat(user.getAccounts()).hasSize(1);
        assertThat(user.getAccounts().get(0)).isSameAs(account);
        assertThat(account.getUser()).isSameAs(user);
    }

    @Test
    void addAccount_여러_계정을_추가할_수_있다() {
        User user = User.builder()
                .email("user@test.com")
                .nickname("테스터")
                .role("ROLE_USER")
                .build();

        UserAccount localAccount = UserAccount.builder()
                .provider(Provider.LOCAL)
                .password("encoded-pw")
                .build();

        UserAccount kakaoAccount = UserAccount.builder()
                .provider(Provider.KAKAO)
                .providerId("kakao-123")
                .build();

        user.addAccount(localAccount);
        user.addAccount(kakaoAccount);

        assertThat(user.getAccounts()).hasSize(2);
    }

    @Test
    void removeAccount_계정을_제거한다() {
        User user = User.builder()
                .email("user@test.com")
                .nickname("테스터")
                .role("ROLE_USER")
                .build();

        UserAccount account = UserAccount.builder()
                .provider(Provider.LOCAL)
                .password("encoded-pw")
                .build();

        user.addAccount(account);
        user.removeAccount(account);

        assertThat(user.getAccounts()).isEmpty();
    }
}
