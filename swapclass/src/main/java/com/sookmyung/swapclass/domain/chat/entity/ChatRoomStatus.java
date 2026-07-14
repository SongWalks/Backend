package com.sookmyung.swapclass.domain.chat.entity;

// ERD chat_room_status 정렬 — 채팅방 UI 단계만 담당.
// 교환 결과(성공/취소/분쟁)는 exchanges.status(exchange_status)가 담당.
public enum ChatRoomStatus {
    CHATTING,    // 일반 채팅 (시간 조율)
    SCHEDULED,   // 교환 시간 확정
    VERIFYING,   // 5분 전 강의 보유 인증
    COUNTDOWN,   // 카운트다운
    DONE         // 교환 종료
}
