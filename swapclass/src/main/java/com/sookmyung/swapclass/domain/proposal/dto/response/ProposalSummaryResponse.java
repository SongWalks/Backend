package com.sookmyung.swapclass.domain.proposal.dto.response;

import com.sookmyung.swapclass.domain.post.entity.Post;
import com.sookmyung.swapclass.domain.proposal.entity.Proposal;

import java.time.LocalDateTime;

/**
 * 보낸/받은 제안 요약 DTO.
 * matchRank: 매칭 순위(1~3), 없으면 null.
 * chatRoomId: ACCEPTED 상태일 때만.
 * receiverPostId: 요청 대상(수신자) 게시글 id — received에선 내 게시글 식별용.
 * receivedCount: 그 수신자 게시글이 받은 PENDING 요청 수.
 * counterpartPost: 상대 게시글 정보(과목명·희망과목) — sent=수신자 글, received=발신자 글.
 */
public record ProposalSummaryResponse(
        Long id,
        String status,
        LocalDateTime expiresAt,
        long remainSeconds,
        Integer matchRank,
        Long chatRoomId,
        Long receiverPostId,
        long receivedCount,
        ProposalPostSummary counterpartPost
) {
    public static ProposalSummaryResponse of(Proposal proposal,
                                             Post counterpartPost,
                                             Integer matchRank,
                                             Long chatRoomId,
                                             long receivedCount) {
        return new ProposalSummaryResponse(
                proposal.getId(),
                proposal.getStatus().name(),
                proposal.getExpiresAt(),
                proposal.getRemainSeconds(),
                matchRank,
                chatRoomId,
                proposal.getReceiverPost().getId(),
                receivedCount,
                ProposalPostSummary.from(counterpartPost)
        );
    }
}
