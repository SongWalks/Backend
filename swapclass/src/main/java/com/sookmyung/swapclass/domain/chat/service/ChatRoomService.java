package com.sookmyung.swapclass.domain.chat.service;

import com.sookmyung.swapclass.domain.chat.dto.ChatMessage;
import com.sookmyung.swapclass.domain.chat.entity.ChatRoom;
import com.sookmyung.swapclass.domain.chat.repository.ChatRoomRepository;
import com.sookmyung.swapclass.global.exception.CustomException;
import com.sookmyung.swapclass.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // 채팅방 조회
    public ChatRoom getChatRoom(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
    }

    // 시스템 메시지 발송 (상태 전이 시 채팅방에 알림)
    public void sendSystemMessage(Long roomId, String content) {
        ChatMessage systemMessage = new ChatMessage();
        systemMessage.setRoomId(roomId);
        systemMessage.setContent(content);
        systemMessage.setType(ChatMessage.MessageType.SYSTEM);

        messagingTemplate.convertAndSend("/topic/chat/" + roomId, systemMessage);
    }
}
