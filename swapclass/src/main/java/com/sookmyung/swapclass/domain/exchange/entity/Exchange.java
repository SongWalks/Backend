package com.sookmyung.swapclass.domain.exchange.entity;

import com.sookmyung.swapclass.domain.post.entity.Post;
import com.sookmyung.swapclass.domain.proposal.entity.Proposal;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "exchanges")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Exchange {

    // 교환 시간 후 72시간 경과 시 자동 '교환 성공' 처리
    private static final long AUTO_CONFIRM_HOURS = 72;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 성사된 제안과 1:1
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposal_id", nullable = false, unique = true)
    private Proposal proposal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_a_id", nullable = false)
    private Post postA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_b_id", nullable = false)
    private Post postB;

    // 교환 확정 시간 (확정 전엔 null)
    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    // 자동 완료 기준 시각 (scheduledAt + 72h)
    @Column(name = "auto_confirm_at")
    private LocalDateTime autoConfirmAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExchangeStatus status;

    @Column(name = "result_a", nullable = false)
    private boolean resultA;   // A측 '교환 성공' 클릭

    @Column(name = "result_b", nullable = false)
    private boolean resultB;   // B측 '교환 성공' 클릭

    @Column(name = "cancel_reason")
    private String cancelReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Builder
    public Exchange(Proposal proposal, Post postA, Post postB) {
        this.proposal = proposal;
        this.postA = postA;
        this.postB = postB;
        this.status = ExchangeStatus.IN_PROGRESS;
        this.resultA = false;
        this.resultB = false;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // 교환 시간 확정 → 자동완료 기준 시각도 함께 세팅
    public void confirmSchedule(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
        this.autoConfirmAt = scheduledAt.plusHours(AUTO_CONFIRM_HOURS);
    }

    public void markResult(boolean isA) {
        if (isA) {
            this.resultA = true;
        } else {
            this.resultB = true;
        }
        if (this.resultA && this.resultB) {
            complete();
        }
    }

    public void complete() {
        this.status = ExchangeStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public void cancel(String reason) {
        this.status = ExchangeStatus.CANCELED;
        this.cancelReason = reason;
    }

    public void toDispute() {
        this.status = ExchangeStatus.DISPUTE;
    }
}
