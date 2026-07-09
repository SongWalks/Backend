package com.sookmyung.swapclass.domain.chat.repository;

import com.sookmyung.swapclass.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // 중복 방 생성 방지 (같은 게시글 + 같은 요청자)
    Optional<ChatRoom> findByPostIdAndRequesterId(Long postId, Long requesterId);

    // 내가 참여 중인 방 목록 (작성자이거나 요청자이거나)
    @Query("SELECT r FROM ChatRoom r " +
           "WHERE r.post.user.id = :userId OR r.requester.id = :userId " +
           "ORDER BY r.id DESC")
    List<ChatRoom> findMyRooms(@Param("userId") Long userId);
}
