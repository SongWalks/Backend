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
public class ExchangeAlarm10MinJob {

    private final ChatRoomRepository chatRoomRepository;
    private final NotificationService notificationService;

    // 1분마다 실행 — 교환 10분 전 알림
    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void send10MinAlarm() {
        List<ChatRoom> scheduledRooms = chatRoomRepository.findByStatus(ChatRoomStatus.SCHEDULED);

        LocalDateTime now = LocalDateTime.now();

        for (ChatRoom room : scheduledRooms) {
            if (room.getExchange().getScheduledAt() == null) continue;

            LocalDateTime scheduledAt = room.getExchange().getScheduledAt();
            LocalDateTime alarmTime = scheduledAt.minusMinutes(10);

            if (now.isAfter(alarmTime.minusSeconds(30)) &&
                    now.isBefore(alarmTime.plusSeconds(30))) {

                notificationService.sendExchangeAlarm10MinNotification(
                        room.getExchange().getPostA().getUser(), room.getId());
                notificationService.sendExchangeAlarm10MinNotification(
                        room.getExchange().getPostB().getUser(), room.getId());

                log.info("ExchangeAlarm10MinJob - roomId: {} 10분 전 알림 발송", room.getId());
            }
        }
    }
}
