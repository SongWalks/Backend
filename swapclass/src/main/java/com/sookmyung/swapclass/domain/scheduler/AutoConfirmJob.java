package com.sookmyung.swapclass.domain.scheduler;

import com.sookmyung.swapclass.domain.chat.entity.ChatRoom;
import com.sookmyung.swapclass.domain.chat.entity.ChatRoomStatus;
import com.sookmyung.swapclass.domain.chat.repository.ChatRoomRepository;
import com.sookmyung.swapclass.domain.exchange.entity.Exchange;
import com.sookmyung.swapclass.domain.exchange.entity.ExchangeStatus;
import com.sookmyung.swapclass.domain.exchange.repository.ExchangeRepository;
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
public class AutoConfirmJob {

    private final ExchangeRepository exchangeRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final NotificationService notificationService;

    // 1시간마다 실행 — 교환 시간 72h 경과 시 자동 완료
    @Scheduled(fixedDelay = 3600000)
    @Transactional
    public void autoConfirm() {
        List<Exchange> exchanges = exchangeRepository
                .findByStatusAndAutoConfirmAtBefore(ExchangeStatus.IN_PROGRESS, LocalDateTime.now());

        for (Exchange exchange : exchanges) {
            exchange.complete();

            // 채팅방 DONE 처리
            chatRoomRepository.findByExchangeId(exchange.getId())
                    .ifPresent(room -> room.changeStatus(ChatRoomStatus.DONE));

            log.info("AutoConfirmJob - exchangeId: {} 자동 완료 처리", exchange.getId());
        }

        if (!exchanges.isEmpty()) {
            log.info("AutoConfirmJob - 총 {}건 자동 완료 처리", exchanges.size());
        }
    }
}
