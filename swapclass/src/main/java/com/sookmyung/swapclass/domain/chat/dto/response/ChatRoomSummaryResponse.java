package com.sookmyung.swapclass.domain.chat.dto.response;

import com.sookmyung.swapclass.domain.chat.entity.ChatMessage;
import com.sookmyung.swapclass.domain.chat.entity.ChatRoom;
import com.sookmyung.swapclass.domain.chat.entity.ChatRoomStatus;
import com.sookmyung.swapclass.domain.chat.entity.MessageType;
import com.sookmyung.swapclass.domain.exchange.entity.Exchange;
import com.sookmyung.swapclass.domain.exchange.entity.ExchangeStatus;
import com.sookmyung.swapclass.domain.post.entity.Post;
import com.sookmyung.swapclass.domain.user.entity.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

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
 *
 * scheduledAt은 서버 내부적으로 KST(Asia/Seoul) 기준 LocalDateTime으로 저장되지만,
 * 응답에서는 UTC Instant(끝에 Z)로 직렬화한다.
 * 프론트가 로컬 타임존으로 정확히 해석할 수 있도록 타임존 정보를 명시하기 위함.
 *
 * 교환 결과:
 *  - status(ChatRoomStatus)는 UI 단계(CHATTING~DONE)만 나타내며, 파기/성공/분쟁을 구분하지 못한다.
 *    (파기·성공완료·분쟁 모두 방 상태는 DONE)
 *  - exchangeStatus(ExchangeStatus)로 종료 사유를 구분한다:
 *    IN_PROGRESS(진행 중) / COMPLETED(성공 완료) / CANCELED(거래 파기) / DISPUTE(분쟁).
 */
public record ChatRoomSummaryResponse(
        Long roomId,
        ChatRoomStatus status,
        ExchangeStatus exchangeStatus,
        Long exchangeId,
        Long partnerId,
        String partnerNickname,
        String myCourseName,
        String partnerCourseName,
        ChatScheduleState scheduleState,
        Instant scheduledAt,
        Instant createdAt,
        String lastMessage,
        MessageType lastMessageType,
        LocalDateTime lastMessageAt
) {
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
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
        LocalDateTime scheduledAt = exchange.getScheduledAt();
        ChatScheduleState scheduleState = scheduledAt != null
                ? ChatScheduleState.CONFIRMED
                : ChatScheduleState.UNDECIDED;

        // KST 기준 LocalDateTime을 UTC Instant로 변환 → 응답 시 "...Z" 형식으로 직렬화됨
        Instant scheduledAtUtc = scheduledAt != null
                ? scheduledAt.atZone(KST).toInstant()
                : null;

        // 방 생성 시간도 KST → UTC 변환 (nullable 아님)
        Instant createdAtUtc = room.getCreatedAt().atZone(KST).toInstant();

        return new ChatRoomSummaryResponse(
                room.getId(),
                room.getStatus(),
                exchange.getStatus(),
                exchange.getId(),
                partner.getId(),
                partner.getNickname(),
                myPost.getDiscardCourse().getName(),
                partnerPost.getDiscardCourse().getName(),
                scheduleState,
                scheduledAtUtc,
                createdAtUtc,
                lastMsg != null ? lastMsg.getContent() : null,
                lastMsg != null ? lastMsg.getType() : null,
                lastMsg != null ? lastMsg.getCreatedAt() : null
        );
    }
}
