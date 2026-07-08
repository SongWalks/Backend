package com.sookmyung.swapclass.domain.notification.entity;

public enum NotificationType {
    // 매칭 및 거래
    MATCH_PROPOSAL,       // 자동 매칭 제안
    MATCH_REQUESTED,      // 교환 요청 수신
    MATCH_ACCEPTED,       // 매칭 수락
    MATCH_REJECTED,       // 매칭 거절
    MATCH_TIMEOUT,        // 매칭 타임아웃 무산

    // 교환 일정
    EXCHANGE_SCHEDULED,   // 교환 시간 확정
    VERIFY_30MIN,         // 교환 30분 전
    VERIFY_10MIN,         // 교환 10분 전
    VERIFY_5MIN,          // 교환 5분 전 (인증 시작)

    // 결과
    SWAP_RESULT,          // 교환 결과
    CANCEL,               // 거래 파기 / 매칭 무산 롤백

    // 기타
    LIKE,                 // 찜 알림
    PENALTY,              // 페널티
    SYSTEM                // 시스템
}
