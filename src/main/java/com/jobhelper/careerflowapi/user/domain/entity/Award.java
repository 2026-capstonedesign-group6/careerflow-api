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

import java.time.LocalDate;

@Entity
@Table(name = "awards")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Award {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column
    private String awardTitle;

    @Column
    private String organizationName;

    @Column
    private LocalDate startDate;

    @Column
    private LocalDate endDate;

    @Builder
    private Award(User user, String awardTitle, String organizationName,
                  LocalDate startDate, LocalDate endDate) {
        this.user = user;
        this.awardTitle = awardTitle;
        this.organizationName = organizationName;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
