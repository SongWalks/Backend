package com.sookmyung.swapclass.domain.match.dto.response;

import com.sookmyung.swapclass.domain.post.dto.response.CourseSummaryResponse;
import com.sookmyung.swapclass.domain.post.dto.response.WantedCourseResponse;

import java.util.List;

/**
 * 추천 매칭 목록 항목(홈 추천 피드 카드 공용).
 * id = 상대 글 id, discardCourse = 상대가 줄 과목(상대 버릴 과목), wantedCourses = 상대 원하는 과목.
 * myDiscardCourse = 내가 줄 과목(= senderPost의 버릴 과목). 카드의 [상대 줄 과목 ↔ 내 줄 과목] 표시용.
 * proposalCount = 상대 글이 받은 대기 중 제안 수.
 * senderPostId = 원클릭 제안 시 사용할 내 게시글(matchRank 최상, 동순위면 최신).
 * matchRank = 상대 버릴 과목이 걸리는 내 희망 순위(1~3).
 * requestStatus = 이미 보낸 요청이면 "PENDING", 아니면 null.
 */
public record RecommendedPostResponse(
        Long id,
        CourseSummaryResponse discardCourse,
        List<WantedCourseResponse> wantedCourses,
        Long senderPostId,
        CourseSummaryResponse myDiscardCourse,
        int proposalCount,
        Integer matchRank,
        String requestStatus
) {
}
