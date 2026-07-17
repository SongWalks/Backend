package com.sookmyung.swapclass.domain.lounge.dto.response;

import com.sookmyung.swapclass.domain.lounge.entity.LoungeComment;

import java.time.LocalDateTime;

public record LoungeCommentCreateResponse(
        Long id,
        Long postId,
        Long userId,
        String content,
        LocalDateTime createdAt
) {
    public static LoungeCommentCreateResponse from(LoungeComment comment) {
        return new LoungeCommentCreateResponse(
                comment.getId(),
                comment.getPost().getId(),
                comment.getUser().getId(),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }
}
