package com.sookmyung.swapclass.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column(name = "penalty_count")
    private int penaltyCount;

    @Column(name = "manner_warning_count")
    private int mannerWarningCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @Column(name = "suspended_until")
    private LocalDateTime suspendedUntil;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.penaltyCount = 0;
        this.mannerWarningCount = 0;
        this.status = UserStatus.ACTIVE;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void suspend(LocalDateTime until) {
        this.status = UserStatus.SUSPENDED;
        this.suspendedUntil = until;
    }

    public void withdraw() {
        this.status = UserStatus.WITHDRAWN;
    }

    public void increasePenalty() {
        this.penaltyCount++;
    }

    public void increaseMannerWarning() {
        this.mannerWarningCount++;
    }

    public boolean isSuspended() {
        return status == UserStatus.SUSPENDED
                && suspendedUntil != null
                && suspendedUntil.isAfter(LocalDateTime.now());
    }
}