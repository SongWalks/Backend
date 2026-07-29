package com.sookmyung.swapclass.domain.chat.dto.response;

import java.util.List;

/**
 * 메시지 목록 조회 응답 (GET /api/chat-rooms/{roomId}/messages).
 * 커서 페이징 결과(최신순). 다음 페이지는 마지막 메시지의 id를 before로 넘긴다.
 */
public record MessageListResponse(
        List<ChatMessageResponse> messages
) {
}
