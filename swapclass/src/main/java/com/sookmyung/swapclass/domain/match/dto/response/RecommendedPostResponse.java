package com.sookmyung.swapclass.domain.match.dto.response;

/**
 * 추천 매칭 목록 항목.
 * matchRank: 매칭 순위(1~3). requestStatus: 이미 보낸 요청이면 "PENDING", 아니면 null.
 */
public record RecommendedPostResponse(
        Long id,
        Integer matchRank,
        String requestStatus
) {
}
