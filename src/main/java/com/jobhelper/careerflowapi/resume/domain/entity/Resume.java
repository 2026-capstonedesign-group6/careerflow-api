package com.jobhelper.careerflowapi.resume.domain.entity;

import com.jobhelper.careerflowapi.global.domain.BaseTimeEntity;
import com.jobhelper.careerflowapi.global.domain.CursorProjection;
import com.jobhelper.careerflowapi.user.domain.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "resumes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Resume extends BaseTimeEntity implements CursorProjection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private int stepProgress;

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<EssayItem> essays = new ArrayList<>();

    @Builder
    private Resume(User user, String title, int stepProgress) {
        this.user = user;
        this.title = title;
        this.stepProgress = stepProgress;
    }

    public void update(String title, int stepProgress) {
        this.title = title;
        this.stepProgress = stepProgress;
    }

    public void addEssay(EssayItem essay) {
        essays.add(essay);
        essay.assignResume(this);
    }

    public void clearEssays() {
        essays.clear();
    }
}
