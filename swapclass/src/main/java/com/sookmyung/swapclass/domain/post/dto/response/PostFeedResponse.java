package com.sookmyung.swapclass.domain.post.dto.response;

import com.sookmyung.swapclass.domain.post.entity.Post;
import com.sookmyung.swapclass.domain.post.entity.PostWantedCourse;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * 피드 카드 한 건: 버릴 과목 → 원하는 과목(1~3순위) + 받은 제안 횟수.
 */
public record PostFeedResponse(
        Long postId,
        CourseSummaryResponse discardCourse,
        List<WantedCourseResponse> wantedCourses,
        int proposalCount,
        LocalDateTime createdAt
) {
    public static PostFeedResponse from(Post post) {
        List<WantedCourseResponse> wanted = post.getWantedCourses().stream()
                .sorted(Comparator.comparingInt(PostWantedCourse::getPriority))
                .map(WantedCourseResponse::from)
                .toList();

        return new PostFeedResponse(
                post.getId(),
                CourseSummaryResponse.from(post.getDiscardCourse()),
                wanted,
                0, // TODO: Proposal 도메인 생기면 실제 받은 제안 횟수로 교체
                post.getCreatedAt()
        );
    }
}
