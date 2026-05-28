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
@Table(name = "family_members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FamilyMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column
    private String relation;

    @Column
    private String name;

    @Column
    private String birthYear;

    @Column
    private String occupation;

    @Builder
    private FamilyMember(User user, String relation, String name,
                         String birthYear, String occupation) {
        this.user = user;
        this.relation = relation;
        this.name = name;
        this.birthYear = birthYear;
        this.occupation = occupation;
    }
}
