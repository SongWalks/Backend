package com.sookmyung.swapclass.domain.chat.dto.response;

import com.sookmyung.swapclass.domain.chat.entity.ChatMessage;
import com.sookmyung.swapclass.domain.chat.entity.MessageType;

import java.time.LocalDateTime;

/**
 * 채팅 메시지 단건 응답.
 * 시스템 메시지는 발신자가 없으므로 senderId가 null일 수 있다.
 */
public record ChatMessageResponse(
        Long id,
        Long senderId,
        String content,
        MessageType type,
        LocalDateTime createdAt
) {
    public static ChatMessageResponse from(ChatMessage message) {
        return new ChatMessageResponse(
                message.getId(),
                message.getSender() != null ? message.getSender().getId() : null,
                message.getContent(),
                message.getType(),
                message.getCreatedAt()
        );
    }
}
