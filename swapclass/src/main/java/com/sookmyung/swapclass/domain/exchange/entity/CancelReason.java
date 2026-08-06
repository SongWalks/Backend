package com.sookmyung.swapclass.domain.exchange.entity;

public enum CancelReason {
    // 상호 합의로 거래 취소
    MUTUAL_TIME_ISSUE,        // 시간 조율 실패
    MUTUAL_COURSE_CHANGE,     // 서로 다른 과목으로 교환하고자 함

    // 인증 정보가 의심됨
    FRAUD_SUSPECT_IMAGE,      // 보유 과목 인증 사진이 의심됨
    FRAUD_DIFFERENT_COURSE,   // 다른 과목 사진 제출

    // 상대방이 거래를 진행하지 않음
    NO_SHOW_COURSE,           // 과목을 버리지 않음
    NO_SHOW_STOPPED,          // 거래를 일방적으로 중단함

    // 기타 (detail로 직접 입력)
    OTHER,

    // 상대방과 연락이 원활하지 않음
    NO_CONTACT
}