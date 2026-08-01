package com.sookmyung.swapclass.domain.post.dto.response;

import java.util.List;

/**
 * 홈화면 추천 매칭 피드: 양방향 교집합으로 뽑힌 타 유저 게시글 목록 + 페이징 정보.
 * 각 카드에는 원클릭 제안용 senderPostId가 포함된다.
 * 비로그인/게시글 없음이면 empty()로 빈 목록을 내려준다(예외 없음).
 */
public record RecommendedFeedResponse(
        List<RecommendedPostResponse> posts,
        int page,
        boolean hasNext
) {
    public static RecommendedFeedResponse of(List<RecommendedPostResponse> posts, int page, boolean hasNext) {
        return new RecommendedFeedResponse(posts, page, hasNext);
    }

    public static RecommendedFeedResponse empty() {
        return new RecommendedFeedResponse(List.of(), 0, false);
    }
}
