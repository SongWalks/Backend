package com.sookmyung.swapclass.domain.lounge.dto.response;

import com.sookmyung.swapclass.domain.lounge.entity.LoungeComment;

import java.time.LocalDateTime;

public record LoungeCommentResponse(
        Long id,
        Long userId,
        String content,
        boolean mine,   // 요청자가 작성한 댓글인지 (삭제 버튼 노출용)
        LocalDateTime createdAt
) {
    public static LoungeCommentResponse from(LoungeComment comment, Long currentUserId) {
        return new LoungeCommentResponse(
                comment.getId(),
                comment.getUser().getId(),
                comment.getContent(),
                currentUserId != null && comment.isAuthor(currentUserId),
                comment.getCreatedAt()
        );
    }
}
