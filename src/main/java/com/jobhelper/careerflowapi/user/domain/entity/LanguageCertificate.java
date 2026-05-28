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
@Table(name = "language_certificates")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LanguageCertificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column
    private String language;

    @Column
    private String examName;

    @Column
    private String score;

    @Column
    private LocalDate examDate;

    @Builder
    private LanguageCertificate(User user, String language, String examName,
                                String score, LocalDate examDate) {
        this.user = user;
        this.language = language;
        this.examName = examName;
        this.score = score;
        this.examDate = examDate;
    }
}
