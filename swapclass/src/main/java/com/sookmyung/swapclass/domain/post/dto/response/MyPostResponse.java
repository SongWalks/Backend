package com.sookmyung.swapclass.domain.post.dto.response;

import com.sookmyung.swapclass.domain.post.entity.Post;
import com.sookmyung.swapclass.domain.post.entity.PostStatus;
import com.sookmyung.swapclass.domain.post.entity.PostWantedCourse;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * 마이페이지 '내 교환 게시글' 카드 한 건. 상태값(status) + 받은 제안 수(proposalCount)를 함께 노출한다.
 */
public record MyPostResponse(
        Long postId,
        PostStatus status,
        CourseSummaryResponse discardCourse,
        List<WantedCourseResponse> wantedCourses,
        int proposalCount,
        LocalDateTime createdAt
) {
    // proposalCount 미지정 시 0. (my-seekers/my-targets 등 타 유저 글은 받은 제안 수가 무의미)
    public static MyPostResponse from(Post post) {
        return from(post, 0L);
    }

    // 받은 제안 수(proposalCount)를 함께 담는다. (내 게시글 목록 /me)
    public static MyPostResponse from(Post post, long proposalCount) {
        List<WantedCourseResponse> wanted = post.getWantedCourses().stream()
                .sorted(Comparator.comparingInt(PostWantedCourse::getPriority))
                .map(WantedCourseResponse::from)
                .toList();

        return new MyPostResponse(
                post.getId(),
                post.getStatus(),
                CourseSummaryResponse.from(post.getDiscardCourse()),
                wanted,
                (int) proposalCount,
                post.getCreatedAt()
        );
    }
}
