package com.sookmyung.swapclass.domain.exchange.service;

import com.sookmyung.swapclass.domain.chat.entity.ChatRoom;
import com.sookmyung.swapclass.domain.chat.entity.ChatRoomStatus;
import com.sookmyung.swapclass.domain.chat.repository.ChatRoomRepository;
import com.sookmyung.swapclass.domain.exchange.dto.request.CancelRequest;
import com.sookmyung.swapclass.domain.exchange.dto.request.ResultRequest;
import com.sookmyung.swapclass.domain.exchange.dto.request.ScheduleRequest;
import com.sookmyung.swapclass.domain.exchange.dto.response.HeroBannerResponse;
import com.sookmyung.swapclass.domain.exchange.dto.response.ResultResponse;
import com.sookmyung.swapclass.domain.exchange.dto.response.ScheduleResponse;
import com.sookmyung.swapclass.domain.exchange.entity.Exchange;
import com.sookmyung.swapclass.domain.exchange.entity.ExchangeStatus;
import com.sookmyung.swapclass.domain.exchange.repository.ExchangeRepository;
import com.sookmyung.swapclass.domain.post.dto.response.CourseSummaryResponse;
import com.sookmyung.swapclass.domain.post.entity.Post;
import com.sookmyung.swapclass.global.exception.CustomException;
import com.sookmyung.swapclass.global.exception.ErrorCode;
import com.sookmyung.swapclass.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExchangeService {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final ExchangeRepository exchangeRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final NotificationService notificationService;

    // 교환 시간 확정
    @Transactional
    public ScheduleResponse confirmSchedule(Long exchangeId, Long userId, ScheduleRequest request) {
        Exchange exchange = getExchangeAndValidateParticipant(exchangeId, userId);


        // 프론트가 보낸 UTC 절대시각(Z 포함)을 KST 벽시계 시간으로 변환해 저장
        // (서버 전체가 KST LocalDateTime 기준으로 저장·비교하므로)
        LocalDateTime scheduledAtKst = request.getScheduledAt()
                .atZoneSameInstant(KST)
                .toLocalDateTime();

        // 미래 시간 검증 (KST 기준)
        if (scheduledAtKst.isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.INVALID_SCHEDULE_TIME);
        }

        // 채팅방 먼저 가져오기
        ChatRoom chatRoom = getChatRoomByExchange(exchangeId);

        // 이미 SCHEDULED 상태면 중복 방지
        if (chatRoom.getStatus() == ChatRoomStatus.SCHEDULED) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        exchange.confirmSchedule(scheduledAtKst);
        // 채팅방 상태 → SCHEDULED
        chatRoom.changeStatus(ChatRoomStatus.SCHEDULED);

        // 양측 알림 발송 (KST 기준 표시)
        String scheduledTime = scheduledAtKst
                .format(DateTimeFormatter.ofPattern("MM월 dd일 HH시 mm분"));
        notificationService.sendExchangeScheduledNotification(
                exchange.getPostA().getUser(), scheduledTime, chatRoom.getId());
        notificationService.sendExchangeScheduledNotification(
                exchange.getPostB().getUser(), scheduledTime, chatRoom.getId());

        // 저장된 KST 값을 다시 UTC Instant로 변환해 응답 (Z 포함, 입력과 동일 절대시각)
        return new ScheduleResponse(toUtc(exchange.getScheduledAt()), toUtc(exchange.getAutoConfirmAt()));
    }

    // KST LocalDateTime → UTC Instant (응답 직렬화 시 "...Z" 형식)
    private Instant toUtc(LocalDateTime kst) {
        return kst == null ? null : kst.atZone(KST).toInstant();
    }

    // 교환 결과 선택 (SUCCESS / FAIL)
    @Transactional
    public ResultResponse selectResult(Long exchangeId, Long userId, ResultRequest request) {
        Exchange exchange = getExchangeAndValidateParticipant(exchangeId, userId);
        ChatRoom chatRoom = getChatRoomByExchange(exchangeId);

        // 내가 A인지 B인지 판별
        boolean isA = exchange.getPostA().getUser().getId().equals(userId);

        // 이미 완료된 교환인지 확인
        if (chatRoom.getStatus() == ChatRoomStatus.DONE) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }
        
        if (request.getSuccess()) {
            // SUCCESS 선택
            exchange.markResult(isA);
            // 양측 모두 SUCCESS면 COMPLETED (Exchange.markResult 내부에서 처리)
            if (exchange.getStatus() == ExchangeStatus.COMPLETED) {
                chatRoom.changeStatus(ChatRoomStatus.DONE);
                // 양측 게시글도 완료 처리 (IN_EXCHANGE → COMPLETED)
                exchange.getPostA().markCompleted();
                exchange.getPostB().markCompleted();
                return new ResultResponse("COMPLETED", "교환이 완료되었습니다!");
            }
            return new ResultResponse("IN_PROGRESS", "상대방의 결과 선택을 기다리는 중입니다.");
        } else {
            // FAIL 선택 → DISPUTE
            exchange.toDispute();
            chatRoom.changeStatus(ChatRoomStatus.DONE);
            return new ResultResponse("DISPUTE", "분쟁이 접수되었습니다. 사후 인증을 진행해주세요.");
        }
    }

    // 거래 파기
    @Transactional
    public void cancelExchange(Long exchangeId, Long userId, CancelRequest request) {
        Exchange exchange = getExchangeAndValidateParticipant(exchangeId, userId);
        ChatRoom chatRoom = getChatRoomByExchange(exchangeId);

        // 이미 파기된 교환인지 확인
        if (chatRoom.getStatus() == ChatRoomStatus.DONE) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        exchange.cancel(request.getReason() +
                (request.getDetail() != null ? " - " + request.getDetail() : ""));
        chatRoom.changeStatus(ChatRoomStatus.DONE);

        // 양측 게시글 MATCHABLE 롤백
        exchange.getPostA().rollbackToMatchable();
        exchange.getPostB().rollbackToMatchable();

        // 양측 알림 발송
        notificationService.sendMatchRollbackNotification(exchange.getPostA().getUser());
        notificationService.sendMatchRollbackNotification(exchange.getPostB().getUser());
    }

    // 홈 히어로 배너: 시간 확정된 진행 중 교환 중 가장 임박한 1건. 없으면(비로그인 포함) null.
    public HeroBannerResponse getHomeHeroBanner(Long userId) {
        if (userId == null) {
            return null;
        }
        List<Exchange> exchanges = exchangeRepository.findParticipatingWithSchedule(
                ExchangeStatus.IN_PROGRESS, userId, LocalDateTime.now(KST), PageRequest.of(0, 1));
        if (exchanges.isEmpty()) {
            return null;
        }
        Exchange exchange = exchanges.get(0);
        Long chatRoomId = chatRoomRepository.findByExchangeId(exchange.getId())
                .map(ChatRoom::getId)
                .orElse(null);

        // 내 게시글/상대 게시글 판별 → 넘기는 과목/받는 과목
        boolean isA = exchange.getPostA().getUser().getId().equals(userId);
        Post myPost = isA ? exchange.getPostA() : exchange.getPostB();
        Post partnerPost = isA ? exchange.getPostB() : exchange.getPostA();

        long remainSeconds = Math.max(
                Duration.between(LocalDateTime.now(), exchange.getScheduledAt()).getSeconds(), 0);
        return new HeroBannerResponse(
                exchange.getId(),
                chatRoomId,
                CourseSummaryResponse.from(myPost.getDiscardCourse()),
                CourseSummaryResponse.from(partnerPost.getDiscardCourse()),
                toUtc(exchange.getScheduledAt()),
                remainSeconds);
    }

    // ─── private 헬퍼 ────────────────────────────────────────

    private Exchange getExchangeAndValidateParticipant(Long exchangeId, Long userId) {
        Exchange exchange = exchangeRepository.findById(exchangeId)
                .orElseThrow(() -> new CustomException(ErrorCode.EXCHANGE_NOT_FOUND));

        // 참여자 확인
        boolean isParticipant = exchange.getPostA().getUser().getId().equals(userId)
                || exchange.getPostB().getUser().getId().equals(userId);
        if (!isParticipant) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        return exchange;
    }

    private ChatRoom getChatRoomByExchange(Long exchangeId) {
        return chatRoomRepository.findByExchangeId(exchangeId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
    }

    private static final String COUNTDOWN_READY_PREFIX = "countdown:ready:";
    private static final int COUNTDOWN_SECONDS = 10;
    private final RedisTemplate<String, String> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public Map<String, Object> readyCountdown(Long exchangeId, Long userId) {
        Exchange exchange = getExchangeAndValidateParticipant(exchangeId, userId);
        ChatRoom chatRoom = getChatRoomByExchange(exchangeId);

        // Redis에 준비 완료 저장
        String redisKey = COUNTDOWN_READY_PREFIX + exchangeId + ":" + userId;
        redisTemplate.opsForValue().set(redisKey, "ready", 10, TimeUnit.MINUTES);

        // 상대방 userId 찾기
        Long partnerUserId = exchange.getPostA().getUser().getId().equals(userId)
                ? exchange.getPostB().getUser().getId()
                : exchange.getPostA().getUser().getId();

        // 상대방도 준비됐는지 확인
        String partnerRedisKey = COUNTDOWN_READY_PREFIX + exchangeId + ":" + partnerUserId;
        boolean partnerReady = Boolean.TRUE.equals(redisTemplate.hasKey(partnerRedisKey));

        if (partnerReady) {
            // 양쪽 모두 준비됐으면 카운트다운 시작
            LocalDateTime countdownEndsAt = LocalDateTime.now(ZoneOffset.UTC).plusSeconds(COUNTDOWN_SECONDS);
            exchange.startCountdown(countdownEndsAt);

            // Redis 정리
            redisTemplate.delete(redisKey);
            redisTemplate.delete(partnerRedisKey);

            // WebSocket으로 카운트다운 시작 알림
            messagingTemplate.convertAndSend(
                    "/topic/chat-rooms/" + chatRoom.getId(),
                    Map.of("type", "COUNTDOWN_START", "countdownEndsAt", countdownEndsAt.toString())
            );

            return Map.of("status", "COUNTDOWN_STARTED", "countdownEndsAt", countdownEndsAt.toString());
        }

        return Map.of("status", "WAITING", "message", "상대방 대기 중입니다.");
    }
}
