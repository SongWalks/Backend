package com.sookmyung.swapclass.domain.post.dto.response;

import com.sookmyung.swapclass.domain.post.entity.Post;
import com.sookmyung.swapclass.domain.post.entity.PostStatus;
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
        PostStatus status,
        LocalDateTime createdAt
) {
    // proposalCount 미지정 시 0. (추천 피드 등 제안 수가 불필요한 경우)
    public static PostFeedResponse from(Post post) {
        return from(post, 0L);
    }

    // 받은 제안 수(proposalCount)를 함께 담는다.
    public static PostFeedResponse from(Post post, long proposalCount) {
        List<WantedCourseResponse> wanted = post.getWantedCourses().stream()
                .sorted(Comparator.comparingInt(PostWantedCourse::getPriority))
                .map(WantedCourseResponse::from)
                .toList();

        return new PostFeedResponse(
                post.getId(),
                CourseSummaryResponse.from(post.getDiscardCourse()),
                wanted,
                (int) proposalCount,
                post.getStatus(),
                post.getCreatedAt()
        );
    }
}
