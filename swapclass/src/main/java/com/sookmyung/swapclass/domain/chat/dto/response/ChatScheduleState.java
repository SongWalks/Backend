package com.sookmyung.swapclass.domain.chat.dto.response;

/**
 * 채팅방 목록에서 교환 가능 시간의 제안 여부.
 * UNDECIDED : 아직 교환 가능 시간이 정해지지 않음 ("미정")
 * PROPOSED  : 교환 가능 시간이 제안됨 ("교환 가능 시간을 제안했습니다", 30분 타이머 동작)
 */
public enum ChatScheduleState {
    UNDECIDED,
    PROPOSED
}
