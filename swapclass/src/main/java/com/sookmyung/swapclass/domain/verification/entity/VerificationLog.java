package com.sookmyung.swapclass.domain.verification.entity;

import com.sookmyung.swapclass.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "verification_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VerificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "exchange_id", nullable = false)
    private Long exchangeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "verify_type", nullable = false)
    private VerifyType verifyType;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "qr_valid")
    private Boolean qrValid;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerifyStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public VerificationLog(Long exchangeId, User user, VerifyType verifyType,
                           String imageUrl) {
        this.exchangeId = exchangeId;
        this.user = user;
        this.verifyType = verifyType;
        this.imageUrl = imageUrl;
        this.status = VerifyStatus.PENDING;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void pass() {
        this.qrValid = true;
        this.status = VerifyStatus.PASSED;
        this.verifiedAt = LocalDateTime.now();
    }

    public void fail() {
        this.qrValid = false;
        this.status = VerifyStatus.FAILED;
        this.verifiedAt = LocalDateTime.now();
    }

    public void markSuspicious() {
        this.status = VerifyStatus.SUSPICIOUS;
    }
}
