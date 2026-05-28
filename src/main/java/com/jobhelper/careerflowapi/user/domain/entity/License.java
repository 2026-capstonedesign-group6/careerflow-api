package com.jobhelper.careerflowapi.user.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "licenses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class License {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column
    private String licenseName;

    @Column
    private String licenseNumber;

    @Column
    private String grade;

    @Column
    private String issuingOrganization;

    @Builder
    private License(User user, String licenseName, String licenseNumber,
                    String grade, String issuingOrganization) {
        this.user = user;
        this.licenseName = licenseName;
        this.licenseNumber = licenseNumber;
        this.grade = grade;
        this.issuingOrganization = issuingOrganization;
    }
}
