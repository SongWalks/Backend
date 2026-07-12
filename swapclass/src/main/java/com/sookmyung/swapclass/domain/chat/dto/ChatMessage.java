package com.sookmyung.swapclass.domain.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessage {

    private Long roomId;
    private Long senderId;
    private String content;
    private MessageType type;
    private LocalDateTime createdAt;

    public enum MessageType {
        TEXT,
        SYSTEM,
        QR_NOTICE,
        RESULT_NOTICE
    }
}
