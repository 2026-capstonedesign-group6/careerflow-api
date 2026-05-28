package com.jobhelper.careerflowapi.experience.domain.entity;

import com.jobhelper.careerflowapi.global.domain.BaseTimeEntity;
import com.jobhelper.careerflowapi.global.domain.CursorProjection;
import com.jobhelper.careerflowapi.user.domain.entity.User;
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
@Table(name = "experiences")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Experience extends BaseTimeEntity implements CursorProjection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String situation;

    @Column(length = 2000)
    private String task;

    @Column(length = 2000)
    private String action;

    @Column(length = 2000)
    private String result;

    @Column
    private String category;

    @Column
    private LocalDate activityDate;

    @Builder
    private Experience(User user, String title, String situation, String task,
                       String action, String result, String category, LocalDate activityDate) {
        this.user = user;
        this.title = title;
        this.situation = situation;
        this.task = task;
        this.action = action;
        this.result = result;
        this.category = category;
        this.activityDate = activityDate;
    }

    public void update(String title, String situation, String task,
                       String action, String result, String category, LocalDate activityDate) {
        this.title = title;
        this.situation = situation;
        this.task = task;
        this.action = action;
        this.result = result;
        this.category = category;
        this.activityDate = activityDate;
    }
}
