package com.sookmyung.swapclass.domain.exchange.dto.response;

import com.sookmyung.swapclass.domain.post.dto.response.CourseSummaryResponse;

import java.time.LocalDateTime;

/**
 * 홈화면 히어로 배너: 시간이 확정된 진행 중 교환 중 가장 임박한 1건.
 * chatRoomId는 [교환 채팅방 입장하기] deep link용. remainSeconds는 교환까지 남은 초(과거면 0).
 * myCourse = 내가 넘기는 과목, partnerCourse = 내가 받는 과목(배너 문구에 노출).
 */
public record HeroBannerResponse(
        Long exchangeId,
        Long chatRoomId,
        CourseSummaryResponse myCourse,
        CourseSummaryResponse partnerCourse,
        LocalDateTime scheduledAt,
        long remainSeconds
) {
}
