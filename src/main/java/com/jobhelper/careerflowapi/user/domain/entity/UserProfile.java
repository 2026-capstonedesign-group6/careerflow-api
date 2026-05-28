package com.jobhelper.careerflowapi.user.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "user_profiles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column
    private String name;

    @Column
    private String phone;

    @Column
    private LocalDate birthDate;

    @Column
    private String address;

    @Column
    private LocalDate militaryStartDate;

    @Column
    private LocalDate militaryEndDate;

    @Column
    private String militaryType;

    @Column
    private String militaryExemptionReason;

    @Column(columnDefinition = "TEXT")
    private String starSituation;

    @Column(columnDefinition = "TEXT")
    private String starTask;

    @Column(columnDefinition = "TEXT")
    private String starAction;

    @Column(columnDefinition = "TEXT")
    private String starResult;

    @Builder
    private UserProfile(User user, String name, String phone, LocalDate birthDate, String address) {
        this.user = user;
        this.name = name;
        this.phone = phone;
        this.birthDate = birthDate;
        this.address = address;
    }

    public void update(String name, String phone, LocalDate birthDate, String address,
                       LocalDate militaryStartDate, LocalDate militaryEndDate,
                       String militaryType, String militaryExemptionReason,
                       String starSituation, String starTask, String starAction, String starResult) {
        this.name = name;
        this.phone = phone;
        this.birthDate = birthDate;
        this.address = address;
        this.militaryStartDate = militaryStartDate;
        this.militaryEndDate = militaryEndDate;
        this.militaryType = militaryType;
        this.militaryExemptionReason = militaryExemptionReason;
        this.starSituation = starSituation;
        this.starTask = starTask;
        this.starAction = starAction;
        this.starResult = starResult;
    }
}
