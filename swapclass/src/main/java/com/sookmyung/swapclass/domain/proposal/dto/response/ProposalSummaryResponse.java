package com.sookmyung.swapclass.domain.proposal.dto.response;

import com.sookmyung.swapclass.domain.proposal.entity.Proposal;

import java.time.LocalDateTime;

/**
 * 보낸/받은/상세 제안 응답에서 공통으로 쓰는 요약 DTO.
 * matchRank는 매칭 순위(1~3), 없으면 null.
 * chatRoomId는 ACCEPTED 상태일 때만 채워진다.
 */
public record ProposalSummaryResponse(
        Long id,
        String status,
        LocalDateTime expiresAt,
        long remainSeconds,
        Integer matchRank,
        Long chatRoomId
) {
    public static ProposalSummaryResponse of(Proposal proposal, Integer matchRank, Long chatRoomId) {
        return new ProposalSummaryResponse(
                proposal.getId(),
                proposal.getStatus().name(),
                proposal.getExpiresAt(),
                proposal.getRemainSeconds(),
                matchRank,
                chatRoomId
        );
    }
}
