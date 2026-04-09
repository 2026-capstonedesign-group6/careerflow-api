package com.jobhelper.careerflowapi.auth.domain.verification;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PendingSignupTest {

    @Test
    void of_초기_attemptCount는_1이다() {
        PendingSignup pending = PendingSignup.of("user@test.com", "닉네임", "encoded-pw");

        assertThat(pending.getEmail()).isEqualTo("user@test.com");
        assertThat(pending.getNickname()).isEqualTo("닉네임");
        assertThat(pending.getEncodedPassword()).isEqualTo("encoded-pw");
        assertThat(pending.getAttemptCount()).isEqualTo(1);
    }

    @Test
    void withNewAttempt_attemptCount가_1_증가한다() {
        PendingSignup original = PendingSignup.of("user@test.com", "old-nick", "old-pw");

        PendingSignup updated = original.withNewAttempt("new-nick", "new-pw");

        assertThat(updated.getAttemptCount()).isEqualTo(2);
        assertThat(updated.getNickname()).isEqualTo("new-nick");
        assertThat(updated.getEncodedPassword()).isEqualTo("new-pw");
        assertThat(updated.getEmail()).isEqualTo("user@test.com");
    }

    @Test
    void isBlocked_attemptCount가_5이하면_false를_반환한다() {
        PendingSignup pending = new PendingSignup("user@test.com", "nick", "pw", 5);

        assertThat(pending.isBlocked()).isFalse();
    }

    @Test
    void isBlocked_attemptCount가_6이상이면_true를_반환한다() {
        PendingSignup pending = new PendingSignup("user@test.com", "nick", "pw", 6);

        assertThat(pending.isBlocked()).isTrue();
    }

    @Test
    void ttlSeconds_600을_반환한다() {
        assertThat(PendingSignup.ttlSeconds()).isEqualTo(600L);
    }
}
