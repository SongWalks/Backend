package com.sookmyung.swapclass.domain.post.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PostStatus {

    MATCHABLE("매칭 전"),
    IN_EXCHANGE("교환 중"),
    COMPLETED("교환 완료"),
    DELETED("삭제됨");        // soft delete

    private final String description;
}
