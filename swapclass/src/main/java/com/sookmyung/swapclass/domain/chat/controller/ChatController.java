package com.sookmyung.swapclass.domain.chat.controller;

import com.sookmyung.swapclass.domain.chat.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    // 클라이언트 발행: /app/chat/{roomId}/send
    // 서버 구독 전달: /topic/chat/{roomId}
    @MessageMapping("/chat/{roomId}/send")
    public void sendMessage(
            @DestinationVariable Long roomId,
            @Payload ChatMessage message) {

        // createdAt 서버 시간으로 설정 (추후 DTO에 setter 추가 필요)
        messagingTemplate.convertAndSend(
                "/topic/chat/" + roomId,
                message
        );
    }
}
