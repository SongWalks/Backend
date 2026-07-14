package com.sookmyung.swapclass.domain.chat.repository;

import com.sookmyung.swapclass.domain.chat.entity.ChatRoom;
import com.sookmyung.swapclass.domain.chat.entity.ChatRoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // exchange와 1:1 — 중복 방 방지 / 교환으로 방 찾기
    Optional<ChatRoom> findByExchangeId(Long exchangeId);

    // 내가 참여한 방 목록 (교환의 두 게시글 작성자 중 하나가 나)
    @Query("SELECT r FROM ChatRoom r " +
           "WHERE r.exchange.postA.user.id = :userId " +
           "   OR r.exchange.postB.user.id = :userId " +
           "ORDER BY r.id DESC")
    List<ChatRoom> findMyRooms(@Param("userId") Long userId);
    List<ChatRoom> findByStatus(ChatRoomStatus status);
}
