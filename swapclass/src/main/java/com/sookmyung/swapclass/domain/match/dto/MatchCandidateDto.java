package com.sookmyung.swapclass.domain.match.dto;

import java.time.LocalDateTime;

/**
 * 추천 매칭 후보 1건 (쿼리 결과용 내부 DTO).
 * 하나의 내 게시글(A) 기준으로 매칭된 상대 게시글(B)의 id·매칭순위·등록시각.
 * matchRank = 상대 버릴 과목이 걸리는 내 희망 순위(1~3).
 */
public record MatchCandidateDto(
        Long postId,
        Integer matchRank,
        LocalDateTime createdAt
) {
}
