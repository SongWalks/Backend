package com.sookmyung.swapclass.domain.proposal.entity;

import com.sookmyung.swapclass.domain.post.entity.Post;
import com.sookmyung.swapclass.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "proposals")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Proposal {

    // 액션(제안) 후 30분 무응답 시 자동 만료
    private static final long EXPIRE_MINUTES = 30;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 제안을 보낸 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_user_id", nullable = false)
    private User sender;

    // 제안을 받은 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_user_id", nullable = false)
    private User receiver;

    // 보낸 사람의 게시글
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_post_id", nullable = false)
    private Post senderPost;

    // 받는 사람의 게시글
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_post_id", nullable = false)
    private Post receiverPost;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProposalStatus status;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Proposal(User sender, User receiver, Post senderPost, Post receiverPost) {
        this.sender = sender;
        this.receiver = receiver;
        this.senderPost = senderPost;
        this.receiverPost = receiverPost;
        this.status = ProposalStatus.PENDING;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.expiresAt = this.createdAt.plusMinutes(EXPIRE_MINUTES);
    }

    public void accept() {
        this.status = ProposalStatus.ACCEPTED;
    }

    public void reject() {
        this.status = ProposalStatus.REJECTED;
    }

    public void withdraw() {
        this.status = ProposalStatus.WITHDRAWN;
    }

    public void markExpired() {
        this.status = ProposalStatus.EXPIRED;
    }

    public boolean isPending() {
        return this.status == ProposalStatus.PENDING;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }
}
