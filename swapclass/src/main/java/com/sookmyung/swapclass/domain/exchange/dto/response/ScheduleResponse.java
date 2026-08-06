package com.sookmyung.swapclass.domain.exchange.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

/**
 * 서버는 시간을 KST 기준 LocalDateTime으로 저장하지만, 응답에서는 UTC Instant(끝에 Z)로 내려
 * 프론트가 타임존을 정확히 해석하도록 한다. (GET /api/chat-rooms 응답과 동일한 규칙)
 */
@Getter
@AllArgsConstructor
public class ScheduleResponse {
    private Instant scheduledAt;
    private Instant autoConfirmAt;
}
