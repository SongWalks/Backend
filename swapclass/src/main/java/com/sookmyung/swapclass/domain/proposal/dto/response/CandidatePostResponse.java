package com.sookmyung.swapclass.domain.proposal.dto.response;

/**
 * 상대 게시글에 제안 시 사용할 수 있는 내 게시글 항목.
 * matchRank: 대상 과목이 걸리는 내 희망 순위(1~3), 매칭 없으면 null.
 * isAlreadyRequested: 이 게시글로 대상에게 이미 PENDING 요청을 보냈는지.
 */
public record CandidatePostResponse(
        Long id,
        Integer matchRank,
        boolean isAlreadyRequested
) {
}
