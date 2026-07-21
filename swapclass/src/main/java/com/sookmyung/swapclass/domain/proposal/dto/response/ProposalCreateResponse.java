package com.sookmyung.swapclass.domain.proposal.dto.response;

import com.sookmyung.swapclass.domain.proposal.entity.Proposal;

import java.time.LocalDateTime;

/**
 * 교환 제안 보내기 응답. 만료 시각은 발송 + 30분.
 */
public record ProposalCreateResponse(
        Long proposalId,
        LocalDateTime expiresAt
) {
    public static ProposalCreateResponse from(Proposal proposal) {
        return new ProposalCreateResponse(proposal.getId(), proposal.getExpiresAt());
    }
}
