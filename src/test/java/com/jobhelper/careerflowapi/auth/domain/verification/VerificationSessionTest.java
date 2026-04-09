package com.jobhelper.careerflowapi.auth.domain.verification;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VerificationSessionTest {

    @Test
    void of_세션을_생성한다() {
        VerificationSession session = VerificationSession.of("user@test.com", "123456");

        assertThat(session.getEmail()).isEqualTo("user@test.com");
        assertThat(session.getCode()).isEqualTo("123456");
    }

    @Test
    void matches_동일한_코드면_true를_반환한다() {
        VerificationSession session = VerificationSession.of("user@test.com", "123456");

        assertThat(session.matches("123456")).isTrue();
    }

    @Test
    void matches_다른_코드면_false를_반환한다() {
        VerificationSession session = VerificationSession.of("user@test.com", "123456");

        assertThat(session.matches("000000")).isFalse();
    }

    @Test
    void ttlSeconds_600을_반환한다() {
        assertThat(VerificationSession.ttlSeconds()).isEqualTo(600L);
    }
}
