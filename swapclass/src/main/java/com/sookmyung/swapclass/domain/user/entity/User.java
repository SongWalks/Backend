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

    //계정의 현재 상태 (정상/제재로 정지된 상태/ 회원 탈퇴)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    //계정 정지 기간
    @Column(name = "suspended_until")
    private LocalDateTime suspendedUntil;

    //알림 수신 설정 (교환 제안 등 알림 수신 여부)
    @Column(name = "notification_enabled", nullable = false)
    private boolean notificationEnabled;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.penaltyCount = 0;
        this.mannerWarningCount = 0;
        this.status = UserStatus.ACTIVE;
        this.notificationEnabled = true;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateNotificationEnabled(boolean notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
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