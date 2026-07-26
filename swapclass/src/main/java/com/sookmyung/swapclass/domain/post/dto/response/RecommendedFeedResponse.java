package com.sookmyung.swapclass.domain.post.dto.response;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 홈화면 추천 매칭 피드: 양방향 교집합으로 뽑힌 타 유저 게시글 목록 + 페이징 정보.
 * 비로그인/게시글 없음이면 empty()로 빈 목록을 내려준다(예외 없음).
 */
public record RecommendedFeedResponse(
        List<PostFeedResponse> posts,
        int page,
        boolean hasNext
) {
    public static RecommendedFeedResponse from(Page<PostFeedResponse> page) {
        return new RecommendedFeedResponse(page.getContent(), page.getNumber(), page.hasNext());
    }

    public static RecommendedFeedResponse empty() {
        return new RecommendedFeedResponse(List.of(), 0, false);
    }
}
