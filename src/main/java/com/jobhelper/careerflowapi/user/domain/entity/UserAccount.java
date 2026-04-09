package com.jobhelper.careerflowapi.user.domain.entity;

import com.jobhelper.careerflowapi.global.domain.BaseTimeEntity;
import com.jobhelper.careerflowapi.user.domain.enums.Provider;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "user_accounts",
        uniqueConstraints = @UniqueConstraint(columnNames = {"provider", "provider_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserAccount extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider;

    @Column(name = "provider_id")
    private String providerId;  // LOCAL이면 null

    private String password;    // 소셜 로그인이면 null

    @Builder
    private UserAccount(Provider provider, String providerId, String password) {
        this.provider = provider;
        this.providerId = providerId;
        this.password = password;
    }

    void assignUser(User user) {
        this.user = user;
    }

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
}
