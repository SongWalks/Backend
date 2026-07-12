package com.sookmyung.swapclass.domain.proposal.entity;

public enum ProposalStatus {
    PENDING,     // 대기 중
    ACCEPTED,    // 수락됨 (→ Exchange 생성)
    REJECTED,    // 거절됨
    WITHDRAWN,   // 보낸 사람이 철회
    EXPIRED      // 30분 무응답 만료
}
