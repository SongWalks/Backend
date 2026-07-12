package com.sookmyung.swapclass.domain.chat.repository;

import com.sookmyung.swapclass.domain.chat.entity.ChatRoom;
import com.sookmyung.swapclass.domain.chat.entity.ChatRoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByExchangeId(Long exchangeId);

    List<ChatRoom> findByStatus(ChatRoomStatus status);
}
