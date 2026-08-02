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
        String content,
        boolean mine,   // 요청자가 작성한 글인지 (삭제 버튼 노출용)
        int likeCount,
        int commentCount,
        LocalDateTime createdAt
) {
    public static LoungePostSummaryResponse from(LoungePost post, Long currentUserId) {
        return new LoungePostSummaryResponse(
                post.getId(),
                post.getType(),
                post.getCourse().getId(),
                post.getCourse().getName(),
                post.getTitle(),
                post.getContent(),
                currentUserId != null && post.isAuthor(currentUserId),
                post.getLikeCount(),
                post.getCommentCount(),
                post.getCreatedAt()
        );
    }
}
