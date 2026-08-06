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
import org.springframework.data.redis.core.RedisTemplate;
import java.util.concurrent.TimeUnit;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExchangeAlarm10MinJob {

    // scheduledAt은 KST 벽시계로 저장되므로 비교 기준도 KST여야 한다.
    // UTC로 비교하면 9시간 어긋나 ±30초 발송 윈도우에 영원히 진입하지 못한다(알림 누락).
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final ChatRoomRepository chatRoomRepository;
    private final NotificationService notificationService;
    private final RedisTemplate<String, String> redisTemplate;

    // 1분마다 실행 — 교환 10분 전 알림
    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void send10MinAlarm() {
        List<ChatRoom> scheduledRooms = chatRoomRepository.findByStatus(ChatRoomStatus.SCHEDULED);
        LocalDateTime now = LocalDateTime.now(KST);

        for (ChatRoom room : scheduledRooms) {
            if (room.getExchange().getScheduledAt() == null) continue;

            LocalDateTime scheduledAt = room.getExchange().getScheduledAt();
            LocalDateTime alarmTime = scheduledAt.minusMinutes(10);

            if (now.isAfter(alarmTime.minusSeconds(30)) &&
                    now.isBefore(alarmTime.plusSeconds(30))) {

                // 중복 발송 방지
                String alarmKey = "alarm:10min:" + room.getId();
                if (Boolean.TRUE.equals(redisTemplate.hasKey(alarmKey))) continue;

                notificationService.sendExchangeAlarm10MinNotification(
                        room.getExchange().getPostA().getUser(), room.getId());
                notificationService.sendExchangeAlarm10MinNotification(
                        room.getExchange().getPostB().getUser(), room.getId());

                // 발송 완료 표시 (1시간 유지)
                redisTemplate.opsForValue().set(alarmKey, "sent", 60, TimeUnit.MINUTES);

                log.info("ExchangeAlarm10MinJob - roomId: {} 10분 전 알림 발송", room.getId());
            }
        }
    }
}
