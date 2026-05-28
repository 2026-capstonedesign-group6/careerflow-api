package com.jobhelper.careerflowapi.user.domain.entity;

import com.jobhelper.careerflowapi.user.domain.enums.EducationLevel;
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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "educations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Education {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String schoolName;

    @Column
    private String major;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EducationLevel level;

    @Column
    private LocalDate startDate;

    @Column
    private LocalDate endDate;

    @Column(nullable = false)
    private boolean isAttending;

    @Builder
    private Education(User user, String schoolName, String major, EducationLevel level,
                      LocalDate startDate, LocalDate endDate, boolean isAttending) {
        this.user = user;
        this.schoolName = schoolName;
        this.major = major;
        this.level = level;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isAttending = isAttending;
    }
}
