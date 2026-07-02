package com.sookmyung.swapclass.domain.report.entity;

public enum ReportReason {
    FRAUD,          // 허위 양도 / 먹튀
    MONEY_DEMAND,   // 금전 요구 / 부당한 거래 조건 변경
    ABUSE,          // 욕설 및 비매너
    OTHER           // 기타
}