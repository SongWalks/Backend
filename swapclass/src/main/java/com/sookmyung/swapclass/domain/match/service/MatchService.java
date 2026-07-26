package com.sookmyung.swapclass.domain.match.service;

import com.sookmyung.swapclass.domain.match.dto.MatchCandidateDto;
import com.sookmyung.swapclass.domain.match.dto.response.RecommendationResponse;
import com.sookmyung.swapclass.domain.match.dto.response.RecommendedPostResponse;
import com.sookmyung.swapclass.domain.post.entity.Post;
import com.sookmyung.swapclass.domain.post.entity.PostStatus;
import com.sookmyung.swapclass.domain.post.repository.PostRepository;
import com.sookmyung.swapclass.domain.proposal.entity.ProposalStatus;
import com.sookmyung.swapclass.domain.proposal.repository.ProposalRepository;
import com.sookmyung.swapclass.global.exception.CustomException;
import com.sookmyung.swapclass.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 추천 매칭 서비스. 내 MATCHABLE 게시글(들)의 희망 과목 기준 양방향 교환 후보를 추천한다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchService {

    private static final String REQUEST_STATUS_PENDING = "PENDING";

    private final PostRepository postRepository;
    private final ProposalRepository proposalRepository;

    public RecommendationResponse getRecommendations(Long userId, Long postId, int page, int size) {
        List<Post> basisPosts = resolveBasisPosts(userId, postId);

        // 기준 게시글별 후보를 통합. 동일 후보는 최소 matchRank로 dedupe.
        Map<Long, MatchCandidateDto> bestByPostId = new LinkedHashMap<>();
        for (Post basis : basisPosts) {
            List<MatchCandidateDto> candidates = postRepository.findBidirectionalCandidates(
                    basis.getId(), basis.getDiscardCourse().getId(), userId);
            for (MatchCandidateDto candidate : candidates) {
                bestByPostId.merge(candidate.postId(), candidate,
                        (existing, replacement) ->
                                replacement.matchRank() < existing.matchRank() ? replacement : existing);
            }
        }

        // 정렬: matchRank ASC → createdAt DESC
        List<MatchCandidateDto> sorted = bestByPostId.values().stream()
                .sorted(Comparator.comparingInt(MatchCandidateDto::matchRank)
                        .thenComparing(MatchCandidateDto::createdAt, Comparator.reverseOrder()))
                .toList();

        // 수동 페이징
        int total = sorted.size();
        int from = Math.min(page * size, total);
        int to = Math.min(from + size, total);
        List<MatchCandidateDto> pageContent = sorted.subList(from, to);
        boolean hasNext = to < total;

        // 이미 보낸 PENDING 요청의 상대 게시글이면 requestStatus = PENDING
        Long pendingReceiverPostId = proposalRepository
                .findBySenderIdAndStatus(userId, ProposalStatus.PENDING)
                .map(proposal -> proposal.getReceiverPost().getId())
                .orElse(null);

        List<RecommendedPostResponse> posts = pageContent.stream()
                .map(candidate -> new RecommendedPostResponse(
                        candidate.postId(),
                        candidate.matchRank(),
                        candidate.postId().equals(pendingReceiverPostId) ? REQUEST_STATUS_PENDING : null))
                .toList();

        return new RecommendationResponse(posts, hasNext);
    }

    // postId 있으면 해당 내 게시글만, 없으면 내 MATCHABLE 게시글 전체를 기준으로.
    private List<Post> resolveBasisPosts(Long userId, Long postId) {
        if (postId != null) {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
            if (!post.isOwnedBy(userId)) {
                throw new CustomException(ErrorCode.FORBIDDEN);
            }
            if (!post.isMatchable()) {
                throw new CustomException(ErrorCode.POST_NOT_MATCHABLE);
            }
            return List.of(post);
        }
        return postRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, PostStatus.MATCHABLE);
    }
}
