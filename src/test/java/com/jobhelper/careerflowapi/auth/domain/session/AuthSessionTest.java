package com.jobhelper.careerflowapi.auth.domain.session;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthSessionTest {

    @Test
    void of_세션을_생성한다() {
        AuthSession session = AuthSession.of("user@test.com", "refresh-token-abc");

        assertThat(session.getEmail()).isEqualTo("user@test.com");
        assertThat(session.getRefreshToken()).isEqualTo("refresh-token-abc");
    }

    @Test
    void isTheft_저장된_토큰과_다르면_true를_반환한다() {
        AuthSession session = AuthSession.of("user@test.com", "original-token");

        assertThat(session.isTheft("different-token")).isTrue();
    }

    @Test
    void isTheft_저장된_토큰과_같으면_false를_반환한다() {
        AuthSession session = AuthSession.of("user@test.com", "original-token");

        assertThat(session.isTheft("original-token")).isFalse();
    }

    @Test
    void rotate_리프레시_토큰을_새_토큰으로_교체한다() {
        AuthSession session = AuthSession.of("user@test.com", "old-token");

        session.rotate("new-token");

        assertThat(session.getRefreshToken()).isEqualTo("new-token");
    }
}
