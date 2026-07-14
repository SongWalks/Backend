package com.sookmyung.swapclass.domain.post.dto.response;

import com.sookmyung.swapclass.domain.post.entity.Post;
import com.sookmyung.swapclass.domain.post.entity.PostStatus;
import com.sookmyung.swapclass.domain.post.entity.PostWantedCourse;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * 마이페이지 '내 교환 게시글' 카드 한 건. 상태값(status)을 함께 노출한다.
 */
public record MyPostResponse(
        Long postId,
        PostStatus status,
        CourseSummaryResponse discardCourse,
        List<WantedCourseResponse> wantedCourses,
        LocalDateTime createdAt
) {
    public static MyPostResponse from(Post post) {
        List<WantedCourseResponse> wanted = post.getWantedCourses().stream()
                .sorted(Comparator.comparingInt(PostWantedCourse::getPriority))
                .map(WantedCourseResponse::from)
                .toList();

        return new MyPostResponse(
                post.getId(),
                post.getStatus(),
                CourseSummaryResponse.from(post.getDiscardCourse()),
                wanted,
                post.getCreatedAt()
        );
    }
}
