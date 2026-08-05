package com.sookmyung.swapclass.domain.chat.dto.response;

/**
 * 채팅방 목록에서 교환 시간 확정 여부.
 * UNDECIDED : 아직 교환 시간이 확정되지 않음(조율 중). 프론트가 무응답 30분 카운트다운 표시.
 * CONFIRMED : [교환시간 결정]으로 시간 확정됨. scheduledAt(약속 일시)을 표시.
 */
public enum ChatScheduleState {
    UNDECIDED,
    CONFIRMED
}
