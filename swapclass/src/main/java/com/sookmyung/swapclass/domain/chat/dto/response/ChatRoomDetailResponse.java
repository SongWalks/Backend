package com.sookmyung.swapclass.domain.chat.dto.response;

import java.util.List;

/**
 * 채팅방 조회 응답 (GET /api/chat-rooms/{roomId}).
 * 채팅방 상태 + 최신 메시지 목록(커서 페이징, 최신순).
 */
public record ChatRoomDetailResponse(
        ChatRoomResponse room,
        List<ChatMessageResponse> messages
) {
}
