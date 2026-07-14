package com.sookmyung.swapclass.domain.chat.entity;

import com.sookmyung.swapclass.domain.exchange.entity.Exchange;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_rooms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 성사된 교환과 1:1. 참여자·교환시간·결과는 모두 Exchange가 보유.
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exchange_id", nullable = false, unique = true)
    private Exchange exchange;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatRoomStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public ChatRoom(Exchange exchange) {
        this.exchange = exchange;
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
}
