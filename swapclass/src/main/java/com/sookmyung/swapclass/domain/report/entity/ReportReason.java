package com.sookmyung.swapclass.domain.report.entity;

public enum ReportReason {
    MUTUAL_CANCEL,      // 상호 합의로 거래 취소
    FAKE_VERIFICATION,  // 허위 인증 사진 제출
    FAKE_COURSE,        // 허위 과목 등록 / 거래 불이행
    MONEY_DEMAND,       // 금전 요구 또는 부당한 조건 변경
    ABUSE,              // 욕설 및 비매너
    OTHER               // 기타
}