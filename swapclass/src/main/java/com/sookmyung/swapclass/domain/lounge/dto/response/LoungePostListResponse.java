package com.sookmyung.swapclass.domain.lounge.dto.response;

import com.sookmyung.swapclass.domain.lounge.entity.LoungePost;

import java.util.List;

public record LoungePostListResponse(
        List<LoungePostSummaryResponse> posts
) {
    public static LoungePostListResponse from(List<LoungePost> posts, Long currentUserId) {
        return new LoungePostListResponse(
                posts.stream()
                        .map(post -> LoungePostSummaryResponse.from(post, currentUserId))
                        .toList()
        );
    }
}
