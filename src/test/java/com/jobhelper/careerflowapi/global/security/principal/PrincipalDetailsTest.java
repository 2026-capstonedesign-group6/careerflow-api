package com.jobhelper.careerflowapi.global.security.principal;

import com.jobhelper.careerflowapi.global.security.dto.AuthUser;
import com.jobhelper.careerflowapi.user.domain.entity.User;
import com.jobhelper.careerflowapi.user.domain.entity.UserAccount;
import com.jobhelper.careerflowapi.user.domain.enums.Provider;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PrincipalDetailsTest {

    @Test
    void from_User_로컬_계정의_비밀번호를_가져온다() {
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

        PrincipalDetails principal = PrincipalDetails.from(user);

        assertThat(principal.getUsername()).isEqualTo("user@test.com");
        assertThat(principal.getPassword()).isEqualTo("encoded-pw");
        assertThat(principal.getUserId()).isNull();
        assertThat(principal.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_USER");
    }

    @Test
    void from_User_소셜_계정만_있으면_password가_null이다() {
        User user = User.builder()
                .email("social@test.com")
                .nickname("소셜유저")
                .role("ROLE_USER")
                .build();
        UserAccount kakaoAccount = UserAccount.builder()
                .provider(Provider.KAKAO)
                .providerId("kakao-123")
                .build();
        user.addAccount(kakaoAccount);

        PrincipalDetails principal = PrincipalDetails.from(user);

        assertThat(principal.getPassword()).isNull();
    }

    @Test
    void from_AuthUser_비밀번호없이_생성된다() {
        AuthUser authUser = AuthUser.of(1L, "user@test.com", "ROLE_USER");

        PrincipalDetails principal = PrincipalDetails.from(authUser);

        assertThat(principal.getUserId()).isEqualTo(1L);
        assertThat(principal.getUsername()).isEqualTo("user@test.com");
        assertThat(principal.getPassword()).isNull();
        assertThat(principal.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_USER");
    }

    @Test
    void isAccountNonExpired_항상_true를_반환한다() {
        PrincipalDetails principal = PrincipalDetails.from(AuthUser.of(1L, "user@test.com", "ROLE_USER"));

        assertThat(principal.isAccountNonExpired()).isTrue();
    }

    @Test
    void isAccountNonLocked_항상_true를_반환한다() {
        PrincipalDetails principal = PrincipalDetails.from(AuthUser.of(1L, "user@test.com", "ROLE_USER"));

        assertThat(principal.isAccountNonLocked()).isTrue();
    }

    @Test
    void isEnabled_항상_true를_반환한다() {
        PrincipalDetails principal = PrincipalDetails.from(AuthUser.of(1L, "user@test.com", "ROLE_USER"));

        assertThat(principal.isEnabled()).isTrue();
    }
}
