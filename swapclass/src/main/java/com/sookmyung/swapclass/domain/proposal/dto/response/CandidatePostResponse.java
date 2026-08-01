package com.sookmyung.swapclass.domain.proposal.dto.response;

import com.sookmyung.swapclass.domain.post.dto.response.CourseSummaryResponse;
import com.sookmyung.swapclass.domain.post.dto.response.WantedCourseResponse;
import com.sookmyung.swapclass.domain.post.entity.Post;
import com.sookmyung.swapclass.domain.post.entity.PostWantedCourse;

import java.util.Comparator;
import java.util.List;

/**
 * 상대 게시글에 제안 시 선택할 수 있는 내 게시글 항목(선택 화면용).
 * discardCourse = 내 버릴 과목, wantedCourses = 내 원하는 과목(1~3순위).
 * matchRank: 내가 상대 버릴 과목을 원하는 순위(1~3) — 다른 화면과 동일한 의미. 없으면 null.
 * partnerWantRank: 상대가 내 버릴 과목을 원하는 순위(1~3) — 화면 문구 "상대방의 교환 희망 N순위"용. 없으면 null.
 * isAlreadyRequested: 이 게시글로 대상에게 이미 PENDING 요청을 보냈는지.
 */
public record CandidatePostResponse(
        Long id,
        CourseSummaryResponse discardCourse,
        List<WantedCourseResponse> wantedCourses,
        Integer matchRank,
        Integer partnerWantRank,
        boolean isAlreadyRequested
) {
    public static CandidatePostResponse of(Post myPost, Integer matchRank,
                                           Integer partnerWantRank, boolean isAlreadyRequested) {
        List<WantedCourseResponse> wanted = myPost.getWantedCourses().stream()
                .sorted(Comparator.comparingInt(PostWantedCourse::getPriority))
                .map(WantedCourseResponse::from)
                .toList();
        return new CandidatePostResponse(
                myPost.getId(),
                CourseSummaryResponse.from(myPost.getDiscardCourse()),
                wanted,
                matchRank,
                partnerWantRank,
                isAlreadyRequested
        );
    }
}
