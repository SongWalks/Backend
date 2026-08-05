package com.sookmyung.swapclass.domain.post.dto.response;

import com.sookmyung.swapclass.domain.post.entity.Post;
import com.sookmyung.swapclass.domain.post.entity.PostStatus;
import com.sookmyung.swapclass.domain.post.entity.PostWantedCourse;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * 교환 게시글 상세 응답.
 * 버릴 과목 상세 + 원하는 과목 1~3순위 + 본인 글 여부(mine).
 */
public record PostDetailResponse(
        Long postId,
        PostStatus status,
        Long authorId,
        String authorNickname,
        CourseSummaryResponse discardCourse,
        List<WantedCourseResponse> wantedCourses,
        int proposalCount,
        String kakaoLink,
        LocalDateTime createdAt,
        boolean mine
) {
    // proposalCount 미지정 시 0. (제안 수가 불필요한 경우)
    public static PostDetailResponse of(Post post, Long currentUserId) {
        return of(post, currentUserId, 0L);
    }

    // 받은 제안 수(proposalCount)를 함께 담는다.
    public static PostDetailResponse of(Post post, Long currentUserId, long proposalCount) {
        List<WantedCourseResponse> wanted = post.getWantedCourses().stream()
                .sorted(Comparator.comparingInt(PostWantedCourse::getPriority))
                .map(WantedCourseResponse::from)
                .toList();

        return new PostDetailResponse(
                post.getId(),
                post.getStatus(),
                post.getUser().getId(),
                post.getUser().getNickname(),
                CourseSummaryResponse.from(post.getDiscardCourse()),
                wanted,
                (int) proposalCount,
                post.getKakaoLink(),
                post.getCreatedAt(),
                post.isOwnedBy(currentUserId)
        );
    }
}
