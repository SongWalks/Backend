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
public class ExchangeAlarm30MinJob {

    private final ChatRoomRepository chatRoomRepository;
    private final NotificationService notificationService;

    // 1분마다 실행 — 교환 30분 전 알림
    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void send30MinAlarm() {
        List<ChatRoom> scheduledRooms = chatRoomRepository.findByStatus(ChatRoomStatus.SCHEDULED);

        LocalDateTime now = LocalDateTime.now();

        for (ChatRoom room : scheduledRooms) {
            if (room.getExchange().getScheduledAt() == null) continue;

            LocalDateTime scheduledAt = room.getExchange().getScheduledAt();
            LocalDateTime alarmTime = scheduledAt.minusMinutes(30);

            // 현재 시간이 알람 시간 ±1분 이내인 경우 발송
            if (now.isAfter(alarmTime.minusSeconds(30)) &&
                    now.isBefore(alarmTime.plusSeconds(30))) {

                notificationService.sendExchangeAlarm30MinNotification(
                        room.getExchange().getPostA().getUser(), room.getId());
                notificationService.sendExchangeAlarm30MinNotification(
                        room.getExchange().getPostB().getUser(), room.getId());

                log.info("ExchangeAlarm30MinJob - roomId: {} 30분 전 알림 발송", room.getId());
            }
        }
    }
}
