package com.sookmyung.swapclass.domain.scheduler;

import com.sookmyung.swapclass.domain.chat.entity.ChatRoom;
import com.sookmyung.swapclass.domain.chat.entity.ChatRoomStatus;
import com.sookmyung.swapclass.domain.chat.repository.ChatRoomRepository;
import com.sookmyung.swapclass.domain.exchange.entity.Exchange;
import com.sookmyung.swapclass.domain.exchange.entity.ExchangeStatus;
import com.sookmyung.swapclass.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatTimeoutJob {

    private final ChatRoomRepository chatRoomRepository;
    private final NotificationService notificationService;

    // 5분마다 실행 — SCHEDULED 상태에서 교환 시간 30분 초과 무응답 시 자동 취소
    @Scheduled(fixedDelay = 300000)
    @Transactional
    public void timeoutChats() {
        List<ChatRoom> scheduledRooms = chatRoomRepository.findByStatus(ChatRoomStatus.SCHEDULED);

        for (ChatRoom room : scheduledRooms) {
            Exchange exchange = room.getExchange();

            // 교환 시간 + 30분 경과 시 자동 취소
            if (exchange.getScheduledAt() != null &&
                    exchange.getScheduledAt().plusMinutes(30).isBefore(LocalDateTime.now())) {

                exchange.cancel("TIMEOUT");
                room.changeStatus(ChatRoomStatus.DONE);

                // 양측 알림 발송
                notificationService.sendMatchRollbackNotification(
                        exchange.getPostA().getUser());
                notificationService.sendMatchRollbackNotification(
                        exchange.getPostB().getUser());

                log.info("ChatTimeoutJob - roomId: {} 타임아웃 처리", room.getId());
            }
        }
    }
}
