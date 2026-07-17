package com.sookmyung.swapclass.domain.lounge.dto.response;

public record LoungeLikeToggleResponse(
        boolean liked,
        int likeCount
) {}
