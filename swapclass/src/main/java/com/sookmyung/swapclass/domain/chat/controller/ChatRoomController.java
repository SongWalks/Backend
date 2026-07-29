package com.sookmyung.swapclass.domain.chat.controller;

import com.sookmyung.swapclass.domain.chat.dto.response.ChatRoomDetailResponse;
import com.sookmyung.swapclass.domain.chat.dto.response.MessageListResponse;
import com.sookmyung.swapclass.domain.chat.service.ChatRoomService;
import com.sookmyung.swapclass.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat-rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    // #1 채팅방 조회 (상태 + 메시지 내역, 커서 페이징)
    @GetMapping("/{roomId}")
    public ResponseEntity<ApiResponse<ChatRoomDetailResponse>> getChatRoom(
            @PathVariable Long roomId,
            @RequestParam(required = false) Long before,
            @RequestParam(defaultValue = "30") int size,
            @AuthenticationPrincipal Long userId) {
        ChatRoomDetailResponse response = chatRoomService.getChatRoomDetail(roomId, userId, before, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // #2 메시지 목록 조회 (커서 페이징)
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<ApiResponse<MessageListResponse>> getMessages(
            @PathVariable Long roomId,
            @RequestParam(required = false) Long before,
            @RequestParam(defaultValue = "30") int size,
            @AuthenticationPrincipal Long userId) {
        MessageListResponse response = chatRoomService.getMessages(roomId, userId, before, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
