package com.sookmyung.swapclass.domain.chat.repository;

import com.sookmyung.swapclass.domain.chat.entity.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // 커서 페이징 - 첫 페이지 (최신 N개)
    List<ChatMessage> findByChatRoomIdOrderByIdDesc(Long chatRoomId, Pageable pageable);

    // 커서 페이징 - 다음 페이지 (cursor id보다 과거 N개)
    List<ChatMessage> findByChatRoomIdAndIdLessThanOrderByIdDesc(
            Long chatRoomId, Long cursorId, Pageable pageable);

    // 여러 방의 마지막 메시지를 한 번에 조회 (채팅방 목록 미리보기용)
    @Query("SELECT m FROM ChatMessage m WHERE m.id IN (" +
           "SELECT MAX(m2.id) FROM ChatMessage m2 " +
           "WHERE m2.chatRoom.id IN :roomIds GROUP BY m2.chatRoom.id)")
    List<ChatMessage> findLatestMessagesByRoomIds(@Param("roomIds") List<Long> roomIds);
}
