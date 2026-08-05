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
 * 교환 시간:
 *  - scheduleState = UNDECIDED : 시간 미확정(조율 중). scheduledAt 없음.
 *      프론트가 무응답 30분 카운트다운을 자체 계산해 표시.
 *  - scheduleState = CONFIRMED : [교환시간 결정]으로 확정됨.
 *      scheduledAt = 약속된 교환 일시 → 목록에 이 시간을 표시.
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

        // 교환 시간 확정(scheduledAt 존재) 여부로 상태 판정
        ChatScheduleState scheduleState = exchange.getScheduledAt() != null
                ? ChatScheduleState.CONFIRMED
                : ChatScheduleState.UNDECIDED;

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
                lastMsg != null ? lastMsg.getContent() : null,
                lastMsg != null ? lastMsg.getType() : null,
                lastMsg != null ? lastMsg.getCreatedAt() : null
        );
    }
}
