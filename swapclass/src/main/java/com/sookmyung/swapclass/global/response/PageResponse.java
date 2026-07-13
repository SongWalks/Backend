package com.sookmyung.swapclass.global.response;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 페이지네이션 응답 공용 래퍼. Spring Page를 그대로 노출하지 않고
 * 프론트가 쓰기 좋은 필드만 추려서 내려준다.
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext
) {
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext()
        );
    }
}
