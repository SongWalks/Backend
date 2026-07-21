package com.sookmyung.swapclass.domain.proposal.dto.response;

import com.sookmyung.swapclass.domain.post.dto.response.PostDetailResponse;
import com.sookmyung.swapclass.domain.proposal.entity.Proposal;

import java.time.LocalDateTime;

/**
 * 교환 요청 상세. 요청자(sender)·수신자(receiver) 게시글 정보를 함께 반환한다.
 * matchRank는 수신자 희망 순위 기준(1~3).
 */
public record ProposalDetailResponse(
        Long id,
        String status,
        LocalDateTime expiresAt,
        long remainSeconds,
        Integer matchRank,
        PostDetailResponse senderPost,
        PostDetailResponse receiverPost
) {
    public static ProposalDetailResponse of(Proposal proposal, Integer matchRank, Long currentUserId) {
        return new ProposalDetailResponse(
                proposal.getId(),
                proposal.getStatus().name(),
                proposal.getExpiresAt(),
                proposal.getRemainSeconds(),
                matchRank,
                PostDetailResponse.of(proposal.getSenderPost(), currentUserId),
                PostDetailResponse.of(proposal.getReceiverPost(), currentUserId)
        );
    }
}
