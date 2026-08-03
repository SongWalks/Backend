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

    // 내가 참여한 방 목록 - 목록 응답에 필요한 연관을 fetch join으로 한 번에 로딩 (N+1 방지)
    @Query("SELECT r FROM ChatRoom r " +
           "JOIN FETCH r.exchange e " +
           "JOIN FETCH e.postA pa JOIN FETCH pa.user JOIN FETCH pa.discardCourse " +
           "JOIN FETCH e.postB pb JOIN FETCH pb.user JOIN FETCH pb.discardCourse " +
           "WHERE pa.user.id = :userId OR pb.user.id = :userId " +
           "ORDER BY r.id DESC")
    List<ChatRoom> findMyRoomsWithDetails(@Param("userId") Long userId);

    List<ChatRoom> findByStatus(ChatRoomStatus status);
}
