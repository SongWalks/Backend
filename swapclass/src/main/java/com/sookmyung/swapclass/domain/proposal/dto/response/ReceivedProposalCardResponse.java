package com.sookmyung.swapclass.domain.proposal.dto.response;

import com.sookmyung.swapclass.domain.post.dto.response.CourseSummaryResponse;
import com.sookmyung.swapclass.domain.proposal.entity.Proposal;

import java.time.LocalDateTime;

/**
 * 홈화면 '받은 제안함' 카드 한 건.
 * myCourse = 내(수신자) 게시글의 버릴 과목, partnerCourse = 상대(발신자) 게시글의 버릴 과목.
 * matchRank는 내 희망 순위(1~3), 없으면 null. remainSeconds는 30분 타이머 잔여 초.
 */
public record ReceivedProposalCardResponse(
        Long proposalId,
        CourseSummaryResponse myCourse,
        CourseSummaryResponse partnerCourse,
        Integer matchRank,
        LocalDateTime expiresAt,
        long remainSeconds
) {
    public static ReceivedProposalCardResponse of(Proposal proposal, Integer matchRank) {
        return new ReceivedProposalCardResponse(
                proposal.getId(),
                CourseSummaryResponse.from(proposal.getReceiverPost().getDiscardCourse()),
                CourseSummaryResponse.from(proposal.getSenderPost().getDiscardCourse()),
                matchRank,
                proposal.getExpiresAt(),
                proposal.getRemainSeconds()
        );
    }
}
