package com.sookmyung.swapclass.domain.lounge.dto.response;

import com.sookmyung.swapclass.domain.lounge.entity.LoungePost;
import com.sookmyung.swapclass.domain.lounge.entity.LoungePostType;

import java.time.LocalDateTime;

// 라운지 게시글 목록 항목
public record LoungePostSummaryResponse(
        Long id,
        LoungePostType type,
        Long courseId,
        String courseName,
        String title,
        int likeCount,
        int commentCount,
        LocalDateTime createdAt
) {
    public static LoungePostSummaryResponse from(LoungePost post) {
        return new LoungePostSummaryResponse(
                post.getId(),
                post.getType(),
                post.getCourse().getId(),
                post.getCourse().getName(),
                post.getTitle(),
                post.getLikeCount(),
                post.getCommentCount(),
                post.getCreatedAt()
        );
    }
}
