package com.sookmyung.swapclass.domain.chat.dto.response;

import com.sookmyung.swapclass.domain.chat.entity.ChatRoom;
import com.sookmyung.swapclass.domain.chat.entity.ChatRoomStatus;

/**
 * 채팅방 상태 요약. UI 단계(status)와 연결된 교환 ID를 담는다.
 */
public record ChatRoomResponse(
        Long id,
        ChatRoomStatus status,
        Long exchangeId
) {
    public static ChatRoomResponse from(ChatRoom room) {
        return new ChatRoomResponse(
                room.getId(),
                room.getStatus(),
                room.getExchange().getId()
        );
    }
}
