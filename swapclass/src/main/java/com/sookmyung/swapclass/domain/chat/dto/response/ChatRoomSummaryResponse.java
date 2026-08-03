package com.sookmyung.swapclass.domain.chat.dto.response;

import com.sookmyung.swapclass.domain.chat.entity.ChatMessage;
import com.sookmyung.swapclass.domain.chat.entity.ChatRoom;
import com.sookmyung.swapclass.domain.chat.entity.ChatRoomStatus;
import com.sookmyung.swapclass.domain.chat.entity.MessageType;
import com.sookmyung.swapclass.domain.exchange.entity.Exchange;
import com.sookmyung.swapclass.domain.post.entity.Post;
import com.sookmyung.swapclass.domain.user.entity.User;

import java.time.LocalDateTime;

/**
 * 채팅방 목록 한 건 요약 (GET /api/chat-rooms).
 *
 * 과목: myCourseName = 내가 넘기는 과목, partnerCourseName = 상대가 넘기는(= 내가 받는) 과목.
 *
 * 교환 가능 시간:
 *  - scheduleState = UNDECIDED : 미정 (scheduledAt 없음)
 *  - scheduleState = PROPOSED  : "교환 가능 시간을 제안했습니다" + 30분 타이머
 *      scheduledAt        = 제안된 교환 시각
 *      timerExpiresAt     = 제안 응답 마감 시각 (제안 시각 + 30분)
 *      remainSeconds      = 마감까지 남은 초 (0이면 만료/미제안)
 */
public record ChatRoomSummaryResponse(
        Long roomId,
        ChatRoomStatus status,
        Long exchangeId,
        Long partnerId,
        String partnerNickname,
        String myCourseName,
        String partnerCourseName,
        ChatScheduleState scheduleState,
        LocalDateTime scheduledAt,
        LocalDateTime timerExpiresAt,
        long remainSeconds,
        String lastMessage,
        MessageType lastMessageType,
        LocalDateTime lastMessageAt
) {
    /**
     * @param room     내가 참여한 채팅방
     * @param myUserId 조회 요청자 id (A/B 중 어느 쪽인지 판별용)
     * @param lastMsg  해당 방의 마지막 메시지 (없으면 null)
     */
    public static ChatRoomSummaryResponse of(ChatRoom room, Long myUserId, ChatMessage lastMsg) {
        Exchange exchange = room.getExchange();

        Post myPost;
        Post partnerPost;
        if (exchange.getPostA().getUser().getId().equals(myUserId)) {
            myPost = exchange.getPostA();
            partnerPost = exchange.getPostB();
        } else {
            myPost = exchange.getPostB();
            partnerPost = exchange.getPostA();
        }
        User partner = partnerPost.getUser();

        boolean proposed = exchange.isTimeProposed();
        ChatScheduleState scheduleState = proposed ? ChatScheduleState.PROPOSED : ChatScheduleState.UNDECIDED;

        return new ChatRoomSummaryResponse(
                room.getId(),
                room.getStatus(),
                exchange.getId(),
                partner.getId(),
                partner.getNickname(),
                myPost.getDiscardCourse().getName(),
                partnerPost.getDiscardCourse().getName(),
                scheduleState,
                exchange.getScheduledAt(),
                exchange.getTimeProposalExpiresAt(),
                exchange.getTimeProposalRemainSeconds(),
                lastMsg != null ? lastMsg.getContent() : null,
                lastMsg != null ? lastMsg.getType() : null,
                lastMsg != null ? lastMsg.getCreatedAt() : null
        );
    }
}
