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
@Table(name = "other_activities")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OtherActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column
    private String content;

    @Column
    private String organizationName;

    @Column
    private LocalDate startDate;

    @Column
    private LocalDate endDate;

    @Builder
    private OtherActivity(User user, String content, String organizationName,
                          LocalDate startDate, LocalDate endDate) {
        this.user = user;
        this.content = content;
        this.organizationName = organizationName;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
