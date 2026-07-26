package com.sookmyung.swapclass.domain.home.service;

import com.sookmyung.swapclass.domain.exchange.service.ExchangeService;
import com.sookmyung.swapclass.domain.home.dto.response.HomeResponse;
import com.sookmyung.swapclass.domain.notification.service.NotificationService;
import com.sookmyung.swapclass.domain.post.service.PostService;
import com.sookmyung.swapclass.domain.proposal.service.ProposalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 홈화면 통합 조회 조립기. 각 도메인 서비스의 홈 전용 메서드를 모아 한 응답으로 반환한다.
 * userId == null(비로그인)이면 개인화 섹션은 비우고 추천 피드도 빈 결과를 내려준다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomeService {

    private final NotificationService notificationService;
    private final ExchangeService exchangeService;
    private final ProposalService proposalService;
    private final PostService postService;

    public HomeResponse getHome(Long userId, int page, int size) {
        if (userId == null) {
            return new HomeResponse(
                    null,
                    null,
                    List.of(),
                    postService.getRecommendedFeed(null, page, size)
            );
        }

        return new HomeResponse(
                notificationService.getUnreadCount(userId),
                exchangeService.getHomeHeroBanner(userId),
                proposalService.getReceivedProposalCards(userId),
                postService.getRecommendedFeed(userId, page, size)
        );
    }
}
