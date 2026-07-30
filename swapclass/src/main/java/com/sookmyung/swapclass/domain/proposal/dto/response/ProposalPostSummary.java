package com.sookmyung.swapclass.domain.proposal.dto.response;

import com.sookmyung.swapclass.domain.post.dto.response.CourseSummaryResponse;
import com.sookmyung.swapclass.domain.post.dto.response.WantedCourseResponse;
import com.sookmyung.swapclass.domain.post.entity.Post;
import com.sookmyung.swapclass.domain.post.entity.PostWantedCourse;

import java.util.Comparator;
import java.util.List;

/**
 * 요청함 목록에 실리는 상대 게시글 요약: 버릴 과목(과목명 포함) + 희망 과목 1~3순위.
 */
public record ProposalPostSummary(
        Long postId,
        CourseSummaryResponse discardCourse,
        List<WantedCourseResponse> wantedCourses
) {
    public static ProposalPostSummary from(Post post) {
        List<WantedCourseResponse> wanted = post.getWantedCourses().stream()
                .sorted(Comparator.comparingInt(PostWantedCourse::getPriority))
                .map(WantedCourseResponse::from)
                .toList();
        return new ProposalPostSummary(
                post.getId(),
                CourseSummaryResponse.from(post.getDiscardCourse()),
                wanted
        );
    }
}
