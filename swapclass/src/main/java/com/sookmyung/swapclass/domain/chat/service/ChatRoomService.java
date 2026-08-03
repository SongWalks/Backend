package com.sookmyung.swapclass.domain.chat.service;

import com.sookmyung.swapclass.domain.chat.dto.response.ChatMessageResponse;
import com.sookmyung.swapclass.domain.chat.dto.response.ChatRoomDetailResponse;
import com.sookmyung.swapclass.domain.chat.dto.response.ChatRoomResponse;
import com.sookmyung.swapclass.domain.chat.dto.response.ChatRoomSummaryResponse;
import com.sookmyung.swapclass.domain.chat.dto.response.MessageListResponse;
import com.sookmyung.swapclass.domain.chat.entity.ChatMessage;
import com.sookmyung.swapclass.domain.chat.entity.ChatRoom;
import com.sookmyung.swapclass.domain.chat.entity.MessageType;
import com.sookmyung.swapclass.domain.chat.repository.ChatMessageRepository;
import com.sookmyung.swapclass.domain.chat.repository.ChatRoomRepository;
import com.sookmyung.swapclass.domain.exchange.entity.Exchange;
import com.sookmyung.swapclass.global.exception.CustomException;
import com.sookmyung.swapclass.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final com.sookmyung.swapclass.domain.user.repository.UserRepository userRepository;

    // ── #0 내 채팅방 목록 조회 (최근 활동순) ─────────────────────────
    public List<ChatRoomSummaryResponse> getMyChatRooms(Long userId) {
        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        List<ChatRoom> rooms = chatRoomRepository.findMyRoomsWithDetails(userId);
        if (rooms.isEmpty()) {
            return List.of();
        }

        // 각 방의 마지막 메시지를 한 번의 쿼리로 로딩 (roomId → 마지막 메시지)
        List<Long> roomIds = rooms.stream().map(ChatRoom::getId).toList();
        Map<Long, ChatMessage> lastMessageByRoom = chatMessageRepository
                .findLatestMessagesByRoomIds(roomIds).stream()
                .collect(Collectors.toMap(m -> m.getChatRoom().getId(), Function.identity()));

        return rooms.stream()
                .map(room -> ChatRoomSummaryResponse.of(room, userId, lastMessageByRoom.get(room.getId())))
                // 마지막 메시지 시각 내림차순, 메시지 없는 방은 뒤로
                .sorted(Comparator.comparing(
                        ChatRoomSummaryResponse::lastMessageAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    // ── #1 채팅방 조회 (상태 + 메시지 내역, 커서 페이징) ─────────────
    public ChatRoomDetailResponse getChatRoomDetail(Long roomId, Long userId, Long before, int size) {
        ChatRoom room = getRoomAndValidate(roomId, userId);
        List<ChatMessageResponse> messages = fetchMessages(roomId, before, size);
        return new ChatRoomDetailResponse(ChatRoomResponse.from(room), messages);
    }

    // ── #2 메시지 목록 조회 (커서 페이징) ────────────────────────────
    public MessageListResponse getMessages(Long roomId, Long userId, Long before, int size) {
        getRoomAndValidate(roomId, userId);   // 참여자 검증만 수행
        return new MessageListResponse(fetchMessages(roomId, before, size));
    }

    // ── #3 WS 수신 메시지 저장 후 브로드캐스트용 DTO 반환 ─────────────
    @Transactional
    public ChatMessageResponse saveTextMessage(Long roomId, Long userId, String content) {
        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        ChatRoom room = getRoomAndValidate(roomId, userId);

        ChatMessage saved = chatMessageRepository.save(
                ChatMessage.builder()
                        .chatRoom(room)
                        .sender(userRepository.getReferenceById(userId))
                        .type(MessageType.TEXT)
                        .content(content)
                        .build());

        return ChatMessageResponse.from(saved);
    }

    // ── private 헬퍼 ────────────────────────────────────────────────

    // id 기반 커서 페이징. before가 null이면 최신 size개, 있으면 그보다 과거 size개(모두 최신순).
    private List<ChatMessageResponse> fetchMessages(Long roomId, Long before, int size) {
        Pageable pageable = PageRequest.of(0, size);
        List<ChatMessage> messages = (before == null)
                ? chatMessageRepository.findByChatRoomIdOrderByIdDesc(roomId, pageable)
                : chatMessageRepository.findByChatRoomIdAndIdLessThanOrderByIdDesc(roomId, before, pageable);
        return messages.stream().map(ChatMessageResponse::from).toList();
    }

    private ChatRoom getRoomAndValidate(Long roomId, Long userId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        validateParticipant(room, userId);
        return room;
    }

    // 교환 당사자(A/B 게시글 작성자)만 접근 허용. 그 외에는 403.
    private void validateParticipant(ChatRoom room, Long userId) {
        Exchange exchange = room.getExchange();
        boolean isParticipant =
                exchange.getPostA().getUser().getId().equals(userId)
                        || exchange.getPostB().getUser().getId().equals(userId);
        if (!isParticipant) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
    }
}
