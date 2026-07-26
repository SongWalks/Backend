package com.sookmyung.swapclass.domain.match.dto.response;

import java.util.List;

/**
 * 추천 매칭 목록 응답. posts + 다음 페이지 여부.
 */
public record RecommendationResponse(
        List<RecommendedPostResponse> posts,
        boolean hasNext
) {
}
