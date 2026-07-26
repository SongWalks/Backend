package com.sookmyung.swapclass.domain.exchange.dto.response;

import java.time.LocalDateTime;

/**
 * 홈화면 히어로 배너: 시간이 확정된 진행 중 교환 중 가장 임박한 1건.
 * chatRoomId는 [교환 채팅방 입장하기] deep link용. remainSeconds는 교환까지 남은 초(과거면 0).
 */
public record HeroBannerResponse(
        Long exchangeId,
        Long chatRoomId,
        LocalDateTime scheduledAt,
        long remainSeconds
) {
}
