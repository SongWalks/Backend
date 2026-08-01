package com.sookmyung.swapclass.domain.home.dto.response;

import com.sookmyung.swapclass.domain.exchange.dto.response.HeroBannerResponse;
import com.sookmyung.swapclass.domain.match.dto.response.RecommendationResponse;
import com.sookmyung.swapclass.domain.proposal.dto.response.ReceivedProposalCardResponse;

import java.util.List;

/**
 * 홈화면 통합 응답. 비로그인/데이터 없음이면:
 * unreadCount·heroBanner = null, receivedProposals = 빈 목록, recommendedFeed = 빈 결과.
 * recommendedFeed 는 추천 매칭(match 도메인) 응답을 그대로 사용한다.
 */
public record HomeResponse(
        Long unreadCount,
        HeroBannerResponse heroBanner,
        List<ReceivedProposalCardResponse> receivedProposals,
        RecommendationResponse recommendedFeed
) {
}
