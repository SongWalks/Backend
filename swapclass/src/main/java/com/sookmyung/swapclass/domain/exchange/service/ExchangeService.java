package com.sookmyung.swapclass.domain.exchange.service;

import com.sookmyung.swapclass.domain.chat.entity.ChatRoom;
import com.sookmyung.swapclass.domain.chat.entity.ChatRoomStatus;
import com.sookmyung.swapclass.domain.chat.repository.ChatRoomRepository;
import com.sookmyung.swapclass.domain.exchange.dto.request.CancelRequest;
import com.sookmyung.swapclass.domain.exchange.dto.request.ResultRequest;
import com.sookmyung.swapclass.domain.exchange.dto.request.ScheduleRequest;
import com.sookmyung.swapclass.domain.exchange.dto.response.ResultResponse;
import com.sookmyung.swapclass.domain.exchange.dto.response.ScheduleResponse;
import com.sookmyung.swapclass.domain.exchange.entity.Exchange;
import com.sookmyung.swapclass.domain.exchange.entity.ExchangeStatus;
import com.sookmyung.swapclass.domain.exchange.repository.ExchangeRepository;
import com.sookmyung.swapclass.global.exception.CustomException;
import com.sookmyung.swapclass.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExchangeService {

    private final ExchangeRepository exchangeRepository;
    private final ChatRoomRepository chatRoomRepository;

    // 교환 시간 확정
    @Transactional
    public ScheduleResponse confirmSchedule(Long exchangeId, Long userId, ScheduleRequest request) {
        Exchange exchange = getExchangeAndValidateParticipant(exchangeId, userId);

        exchange.confirmSchedule(request.getScheduledAt());

        // 채팅방 상태 → SCHEDULED
        ChatRoom chatRoom = getChatRoomByExchange(exchangeId);
        chatRoom.changeStatus(ChatRoomStatus.SCHEDULED);

        return new ScheduleResponse(exchange.getScheduledAt(), exchange.getAutoConfirmAt());
    }

    // 교환 결과 선택 (SUCCESS / FAIL)
    @Transactional
    public ResultResponse selectResult(Long exchangeId, Long userId, ResultRequest request) {
        Exchange exchange = getExchangeAndValidateParticipant(exchangeId, userId);
        ChatRoom chatRoom = getChatRoomByExchange(exchangeId);

        // 내가 A인지 B인지 판별
        boolean isA = exchange.getPostA().getUser().getId().equals(userId);

        if (request.getSuccess()) {
            // SUCCESS 선택
            exchange.markResult(isA);
            // 양측 모두 SUCCESS면 COMPLETED (Exchange.markResult 내부에서 처리)
            if (exchange.getStatus() == ExchangeStatus.COMPLETED) {
                chatRoom.changeStatus(ChatRoomStatus.DONE);
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

        exchange.cancel(request.getReason() +
                (request.getDetail() != null ? " - " + request.getDetail() : ""));
        chatRoom.changeStatus(ChatRoomStatus.DONE);

        // TODO: 귀책 없는 유저 게시글 MATCHABLE 롤백 + 양측 알림 발송
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
}
