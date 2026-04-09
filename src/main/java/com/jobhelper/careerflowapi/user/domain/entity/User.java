package com.jobhelper.careerflowapi.user.domain.entity;

import com.jobhelper.careerflowapi.global.domain.BaseTimeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserAccount> accounts = new ArrayList<>();

    @Builder
    private User(String email, String nickname, String role) {
        this.email = email;
        this.nickname = nickname;
        this.role = role;
    }

    public void addAccount(UserAccount account) {
        accounts.add(account);
        account.assignUser(this);
    }

    public void removeAccount(UserAccount account) {
        accounts.remove(account);
    }
}
