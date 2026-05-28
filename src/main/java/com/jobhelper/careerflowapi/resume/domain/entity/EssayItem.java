package com.jobhelper.careerflowapi.resume.domain.entity;

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
@Table(name = "essay_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EssayItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @Column(nullable = false, length = 500)
    private String question;

    @Column(length = 3000)
    private String content;

    @Column(nullable = false)
    private int orderIndex;

    @Builder
    private EssayItem(String question, String content, int orderIndex) {
        this.question = question;
        this.content = content;
        this.orderIndex = orderIndex;
    }

    void assignResume(Resume resume) {
        this.resume = resume;
    }
}
