package com.sookmyung.swapclass.domain.lounge.dto.response;

import com.sookmyung.swapclass.domain.lounge.entity.LoungeComment;

import java.time.LocalDateTime;

public record LoungeCommentResponse(
        Long id,
        Long userId,
        String content,
        LocalDateTime createdAt
) {
    public static LoungeCommentResponse from(LoungeComment comment) {
        return new LoungeCommentResponse(
                comment.getId(),
                comment.getUser().getId(),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }
}
