package com.sookmyung.swapclass.domain.exchange.entity;

public enum ExchangeStatus {
    IN_PROGRESS,   // 교환 진행 중
    COMPLETED,     // 교환 성공 완료
    CANCELED,      // 거래 파기/취소
    DISPUTE        // 교환 실패 → 사후 인증(분쟁)
}
