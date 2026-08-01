package com.sookmyung.swapclass.domain.post.dto.response;

import com.sookmyung.swapclass.domain.post.entity.Post;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 홈 추천 피드 카드 한 건 = 상대(후보) 게시글 카드 + 원클릭 제안용 메타데이터.
 * senderPostId = 양방향 매칭된 내 게시글(matchRank 최상, 동순위면 최신). 없으면 null.
 * matchRank = 내가 상대 버릴 과목을 원하는 순위(1~3). 없으면 null.
 */
public record RecommendedPostResponse(
        Long postId,
        CourseSummaryResponse discardCourse,
        List<WantedCourseResponse> wantedCourses,
        int proposalCount,
        LocalDateTime createdAt,
        Long senderPostId,
        Integer matchRank
) {
    public static RecommendedPostResponse of(Post candidate, long proposalCount,
                                             Long senderPostId, Integer matchRank) {
        PostFeedResponse card = PostFeedResponse.from(candidate, proposalCount);
        return new RecommendedPostResponse(
                card.postId(),
                card.discardCourse(),
                card.wantedCourses(),
                card.proposalCount(),
                card.createdAt(),
                senderPostId,
                matchRank
        );
    }
}
