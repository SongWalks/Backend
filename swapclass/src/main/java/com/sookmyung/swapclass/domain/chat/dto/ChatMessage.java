package com.sookmyung.swapclass.domain.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ChatMessage {

    private Long roomId;
    private Long senderId;
    private String content;
    private MessageType type;
    private LocalDateTime createdAt;

    public enum MessageType {
        TEXT,       // 일반 텍스트
        SYSTEM,     // 시스템 메시지
        QR_NOTICE,  // QR 인증 안내
        RESULT_NOTICE // 교환 결과 안내
    }
}
