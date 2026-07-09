package com.sookmyung.swapclass.domain.chat.entity;

public enum ChatRoomStatus {
    CHATTING,    // 대화 중 (교환 조건 협의)
    VERIFYING,   // 상호 확인
    COUNTDOWN,   // 교환 카운트다운
    SWAPPING,    // 교환 실행
    CLOSED       // 종료(교환 완료/취소)
}
