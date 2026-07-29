package com.sookmyung.swapclass.domain.chat.controller;

import com.sookmyung.swapclass.domain.chat.dto.ChatMessage;
import com.sookmyung.swapclass.domain.chat.dto.response.ChatMessageResponse;
import com.sookmyung.swapclass.domain.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRoomService chatRoomService;

    // 클라이언트 발행: /app/chat/{roomId}/send
    // 서버 구독 전달: /topic/chat/{roomId}
    @MessageMapping("/chat/{roomId}/send")
    public void sendMessage(
            @DestinationVariable Long roomId,
            @Payload ChatMessage message,
            SimpMessageHeaderAccessor headerAccessor) {

        // 발신자는 CONNECT 시 StompHandler가 세션에 넣어둔 userId를 신뢰 (payload 위조 방지)
        Long senderId = (Long) headerAccessor.getSessionAttributes().get("userId");

        // DB 저장 + 참여자 검증 → id/senderId/createdAt이 채워진 응답 생성
        ChatMessageResponse saved = chatRoomService.saveTextMessage(roomId, senderId, message.getContent());

        messagingTemplate.convertAndSend("/topic/chat/" + roomId, saved);
    }
}
