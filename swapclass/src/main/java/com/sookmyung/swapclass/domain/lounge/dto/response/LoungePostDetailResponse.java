package com.sookmyung.swapclass.domain.lounge.dto.response;

import com.sookmyung.swapclass.domain.lounge.entity.LoungeComment;
import com.sookmyung.swapclass.domain.lounge.entity.LoungePost;
import com.sookmyung.swapclass.domain.lounge.entity.LoungePostType;

import java.time.LocalDateTime;
import java.util.List;

public record LoungePostDetailResponse(
        Long id,
        LoungePostType type,
        Long courseId,
        String courseName,
        String title,
        String content,
        Long authorId,
        int likeCount,
        int commentCount,
        boolean liked,        // 요청자가 좋아요 눌렀는지
        boolean bookmarked,   // 요청자가 북마크 했는지
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<LoungeCommentResponse> comments
) {
    public static LoungePostDetailResponse of(LoungePost post,
                                              boolean liked,
                                              boolean bookmarked,
                                              List<LoungeComment> comments) {
        return new LoungePostDetailResponse(
                post.getId(),
                post.getType(),
                post.getCourse().getId(),
                post.getCourse().getName(),
                post.getTitle(),
                post.getContent(),
                post.getUser().getId(),
                post.getLikeCount(),
                post.getCommentCount(),
                liked,
                bookmarked,
                post.getCreatedAt(),
                post.getUpdatedAt(),
                comments.stream()
                        .map(LoungeCommentResponse::from)
                        .toList()
        );
    }
}
