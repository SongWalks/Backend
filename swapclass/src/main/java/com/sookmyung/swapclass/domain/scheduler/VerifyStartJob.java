package com.sookmyung.swapclass.domain.scheduler;

import com.sookmyung.swapclass.domain.chat.entity.ChatRoom;
import com.sookmyung.swapclass.domain.chat.entity.ChatRoomStatus;
import com.sookmyung.swapclass.domain.chat.repository.ChatRoomRepository;
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
public class VerifyStartJob {

    private final ChatRoomRepository chatRoomRepository;
    private final NotificationService notificationService;

    // 1분마다 실행 — 교환 5분 전 인증 시작 알림 + 채팅방 VERIFYING 상태 전이
    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void startVerification() {
        List<ChatRoom> scheduledRooms = chatRoomRepository.findByStatus(ChatRoomStatus.SCHEDULED);

        LocalDateTime now = LocalDateTime.now();

        for (ChatRoom room : scheduledRooms) {
            if (room.getExchange().getScheduledAt() == null) continue;

            LocalDateTime scheduledAt = room.getExchange().getScheduledAt();
            LocalDateTime verifyTime = scheduledAt.minusMinutes(5);

            if (now.isAfter(verifyTime.minusSeconds(30)) &&
                    now.isBefore(verifyTime.plusSeconds(30))) {

                // 채팅방 상태 → VERIFYING
                room.changeStatus(ChatRoomStatus.VERIFYING);

                // 양측 알림 발송
                notificationService.sendVerifyStartNotification(
                        room.getExchange().getPostA().getUser(), room.getId());
                notificationService.sendVerifyStartNotification(
                        room.getExchange().getPostB().getUser(), room.getId());

                log.info("VerifyStartJob - roomId: {} VERIFYING 전환 + 5분 전 알림 발송", room.getId());
            }
        }
    }
}
