package com.sookmyung.swapclass.domain.post.dto.response;

import com.sookmyung.swapclass.domain.post.entity.Post;
import com.sookmyung.swapclass.domain.post.entity.PostLike;
import com.sookmyung.swapclass.domain.post.entity.PostStatus;
import com.sookmyung.swapclass.domain.post.entity.PostWantedCourse;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * 내 찜 목록의 카드 한 건.
 * blinded=true 면 거래완료/삭제 글 → 프론트에서 클릭 불가로 흐리게 표시.
 */
public record PostLikeItemResponse(
        Long postId,
        PostStatus status,
        boolean blinded,
        CourseSummaryResponse discardCourse,
        List<WantedCourseResponse> wantedCourses,
        LocalDateTime likedAt
) {
    public static PostLikeItemResponse from(PostLike like) {
        Post post = like.getPost();

        List<WantedCourseResponse> wanted = post.getWantedCourses().stream()
                .sorted(Comparator.comparingInt(PostWantedCourse::getPriority))
                .map(WantedCourseResponse::from)
                .toList();

        // 거래완료·삭제 글은 블라인드 상태
        boolean blinded = post.getStatus() == PostStatus.COMPLETED
                || post.getStatus() == PostStatus.DELETED;

        return new PostLikeItemResponse(
                post.getId(),
                post.getStatus(),
                blinded,
                CourseSummaryResponse.from(post.getDiscardCourse()),
                wanted,
                like.getCreatedAt()
        );
    }
}
