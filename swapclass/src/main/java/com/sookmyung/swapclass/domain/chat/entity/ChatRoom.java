package com.sookmyung.swapclass.domain.chat.entity;

import com.sookmyung.swapclass.domain.post.entity.Post;
import com.sookmyung.swapclass.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "chat_rooms",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_post_requester",
                columnNames = {"post_id", "requester_id"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 매칭된 게시글 (게시글 작성자 = 참여자 A)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    // 교환을 요청한 사람 (참여자 B)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatRoomStatus status;

    // 확정된 교환 시각 (exchange-time API가 세팅, 그전엔 null)
    @Column(name = "exchange_time")
    private LocalDateTime exchangeTime;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public ChatRoom(Post post, User requester) {
        this.post = post;
        this.requester = requester;
        this.status = ChatRoomStatus.CHATTING;   // 생성 시 초기 상태
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // 상태 전이 (상태 머신이 호출)
    public void changeStatus(ChatRoomStatus next) {
        this.status = next;
    }

    public void confirmExchangeTime(LocalDateTime time) {
        this.exchangeTime = time;
    }

    // 이 유저가 방 참여자인지 (구독 인가 체크용)
    public boolean hasParticipant(Long userId) {
        return post.getUser().getId().equals(userId)
                || requester.getId().equals(userId);
    }
}
