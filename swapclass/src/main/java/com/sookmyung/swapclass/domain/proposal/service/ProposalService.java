package com.sookmyung.swapclass.domain.proposal.service;

import com.sookmyung.swapclass.domain.block.repository.UserBlockRepository;
import com.sookmyung.swapclass.domain.chat.entity.ChatRoom;
import com.sookmyung.swapclass.domain.chat.repository.ChatRoomRepository;
import com.sookmyung.swapclass.domain.exchange.repository.ExchangeRepository;
import com.sookmyung.swapclass.domain.match.entity.MatchIgnore;
import com.sookmyung.swapclass.domain.match.repository.MatchIgnoreRepository;
import com.sookmyung.swapclass.domain.notification.service.NotificationService;
import com.sookmyung.swapclass.domain.post.entity.Post;
import com.sookmyung.swapclass.domain.post.entity.PostStatus;
import com.sookmyung.swapclass.domain.post.repository.PostRepository;
import com.sookmyung.swapclass.domain.proposal.dto.request.ProposalCreateRequest;
import com.sookmyung.swapclass.domain.proposal.dto.response.CandidatePostResponse;
import com.sookmyung.swapclass.domain.proposal.dto.response.ProposalCreateResponse;
import com.sookmyung.swapclass.domain.proposal.dto.response.ProposalDetailResponse;
import com.sookmyung.swapclass.domain.proposal.dto.response.ProposalSummaryResponse;
import com.sookmyung.swapclass.domain.proposal.entity.Proposal;
import com.sookmyung.swapclass.domain.proposal.entity.ProposalStatus;
import com.sookmyung.swapclass.domain.proposal.repository.ProposalRepository;
import com.sookmyung.swapclass.domain.user.entity.User;
import com.sookmyung.swapclass.domain.user.repository.UserRepository;
import com.sookmyung.swapclass.global.exception.CustomException;
import com.sookmyung.swapclass.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 교환 제안 도메인 서비스. (#2: 보내기 / 철회 / 제안 가능한 내 게시글 조회)
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProposalService {

    private final ProposalRepository proposalRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final UserBlockRepository userBlockRepository;
    private final ExchangeRepository exchangeRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MatchIgnoreRepository matchIgnoreRepository;
    private final NotificationService notificationService;

    // ─── 제안 보내기 ──────────────────────────────────────────
    @Transactional
    public ProposalCreateResponse createProposal(Long senderUserId, ProposalCreateRequest request) {
        User sender = userRepository.findById(senderUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Post senderPost = getPostOrThrow(request.senderPostId());
        Post receiverPost = getPostOrThrow(request.receiverPostId());

        // 내 게시글로만 제안 가능
        if (!senderPost.isOwnedBy(senderUserId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
        // 본인 게시글에는 제안 불가
        if (receiverPost.isOwnedBy(senderUserId)) {
            throw new CustomException(ErrorCode.CANNOT_PROPOSE_TO_OWN_POST);
        }
        // 양측 모두 매칭 전 상태여야 함
        if (!senderPost.isMatchable() || !receiverPost.isMatchable()) {
            throw new CustomException(ErrorCode.POST_NOT_MATCHABLE);
        }

        User receiver = receiverPost.getUser();
        // 차단 관계(양방향)면 제안 불가
        if (isBlockedBetween(senderUserId, receiver.getId())) {
            throw new CustomException(ErrorCode.BLOCKED_USER);
        }
        // 동시에 진행 중인 PENDING 요청은 1개만
        if (proposalRepository.existsBySenderIdAndStatus(senderUserId, ProposalStatus.PENDING)) {
            throw new CustomException(ErrorCode.PROPOSAL_IN_PROGRESS);
        }

        Proposal proposal = Proposal.builder()
                .sender(sender)
                .receiver(receiver)
                .senderPost(senderPost)
                .receiverPost(receiverPost)
                .build();
        proposalRepository.save(proposal);

        notificationService.sendProposalReceivedNotification(receiver, proposal.getId());

        return ProposalCreateResponse.from(proposal);
    }

    // ─── 제안 철회 ────────────────────────────────────────────
    @Transactional
    public void withdrawProposal(Long userId, Long proposalId) {
        Proposal proposal = getProposalOrThrow(proposalId);
        validateSender(proposal, userId);

        // 대기 중인 요청만 철회 가능 (수락/거절/만료된 건 철회 불가)
        if (!proposal.isPending()) {
            throw new CustomException(ErrorCode.PROPOSAL_NOT_PENDING);
        }

        proposal.withdraw(); // WITHDRAWN → received 목록(PENDING 필터)에서 자동 제외, 요청 권한 복구
    }

    // ─── 제안 거절 ────────────────────────────────────────────
    @Transactional
    public void rejectProposal(Long userId, Long proposalId) {
        Proposal proposal = getProposalOrThrow(proposalId);
        validateReceiver(proposal, userId);

        if (!proposal.isPending()) {
            throw new CustomException(ErrorCode.PROPOSAL_NOT_PENDING);
        }

        proposal.reject();
        // 동일 쌍 재추천 차단 (무한 핑퐁 방지)
        addMatchIgnore(proposal);
        // 거절 알림
        notificationService.sendMatchRejectedNotification(
                proposal.getSender(), proposal.getSenderPost().getId());
    }

    // ─── 보낸 제안 조회 ──────────────────────────────────────
    // 동시에 최대 1개 → 가장 최근 보낸 요청 1건. 없으면 null. ACCEPTED면 chatRoomId 포함.
    public ProposalSummaryResponse getSentProposal(Long userId) {
        return proposalRepository.findFirstBySenderIdOrderByCreatedAtDesc(userId)
                .map(proposal -> ProposalSummaryResponse.of(proposal, null, resolveChatRoomId(proposal)))
                .orElse(null);
    }

    // 수락된 제안만 채팅방으로 연결 (제안 → 교환 → 채팅방)
    private Long resolveChatRoomId(Proposal proposal) {
        if (proposal.getStatus() != ProposalStatus.ACCEPTED) {
            return null;
        }
        return exchangeRepository.findByProposalId(proposal.getId())
                .flatMap(exchange -> chatRoomRepository.findByExchangeId(exchange.getId()))
                .map(ChatRoom::getId)
                .orElse(null);
    }

    // ─── 받은 제안 목록 조회 ─────────────────────────────────
    // 내 게시글에 들어온 대기 중 요청, 만료 임박순. matchRank는 내 희망 순위 기준.
    public List<ProposalSummaryResponse> getReceivedProposals(Long userId) {
        return proposalRepository
                .findByReceiverIdAndStatusOrderByExpiresAtAsc(userId, ProposalStatus.PENDING)
                .stream()
                .map(proposal -> {
                    Integer matchRank = matchRankFor(
                            proposal.getReceiverPost(),
                            proposal.getSenderPost().getDiscardCourse().getId());
                    return ProposalSummaryResponse.of(proposal, matchRank, null);
                })
                .toList();
    }

    // ─── 제안 상세 조회 ──────────────────────────────────────
    // 발신자·수신자 본인만 조회 가능. 상대/내 게시글 정보 함께 반환.
    public ProposalDetailResponse getProposalDetail(Long userId, Long proposalId) {
        Proposal proposal = getProposalOrThrow(proposalId);

        boolean participant = proposal.getSender().getId().equals(userId)
                || proposal.getReceiver().getId().equals(userId);
        if (!participant) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        Integer matchRank = matchRankFor(
                proposal.getReceiverPost(),
                proposal.getSenderPost().getDiscardCourse().getId());
        return ProposalDetailResponse.of(proposal, matchRank, userId);
    }

    // ─── 제안 가능한 내 게시글 조회 ───────────────────────────
    public List<CandidatePostResponse> getCandidates(Long userId, Long targetPostId) {
        Post targetPost = getPostOrThrow(targetPostId);
        Long targetDiscardCourseId = targetPost.getDiscardCourse().getId();

        List<Post> myPosts = postRepository
                .findByUserIdAndStatusOrderByCreatedAtDesc(userId, PostStatus.MATCHABLE);

        return myPosts.stream()
                .map(myPost -> {
                    Integer matchRank = matchRankFor(myPost, targetDiscardCourseId);
                    boolean alreadyRequested = proposalRepository
                            .existsBySenderPostIdAndReceiverPostIdAndStatus(
                                    myPost.getId(), targetPostId, ProposalStatus.PENDING);
                    return new CandidatePostResponse(myPost.getId(), matchRank, alreadyRequested);
                })
                .toList();
    }

    // ─── 공통 헬퍼 ────────────────────────────────────────────

    // 내 게시글의 희망 과목 중 대상 과목이 걸리는 우선순위(1~3), 없으면 null
    private Integer matchRankFor(Post myPost, Long targetDiscardCourseId) {
        return myPost.getWantedCourses().stream()
                .filter(w -> w.getCourse().getId().equals(targetDiscardCourseId))
                .map(w -> (Integer) w.getPriority())
                .findFirst()
                .orElse(null);
    }

    private boolean isBlockedBetween(Long userId, Long otherUserId) {
        return userBlockRepository.existsByBlockerIdAndBlockedId(userId, otherUserId)
                || userBlockRepository.existsByBlockerIdAndBlockedId(otherUserId, userId);
    }

    private Post getPostOrThrow(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }

    private Proposal getProposalOrThrow(Long proposalId) {
        return proposalRepository.findById(proposalId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROPOSAL_NOT_FOUND));
    }

    private void validateSender(Proposal proposal, Long userId) {
        if (!proposal.getSender().getId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
    }

    // 요청 받은 본인(게시글 주인)만 수락/거절 가능
    private void validateReceiver(Proposal proposal, Long userId) {
        if (!proposal.getReceiver().getId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
    }

    // 거절/무산된 게시글 쌍을 정규화해 재추천 차단 목록에 추가 (중복 저장 방지)
    private void addMatchIgnore(Proposal proposal) {
        Long p1 = proposal.getSenderPost().getId();
        Long p2 = proposal.getReceiverPost().getId();
        Long aId = Math.min(p1, p2);
        Long bId = Math.max(p1, p2);
        if (!matchIgnoreRepository.existsByPostAIdAndPostBId(aId, bId)) {
            matchIgnoreRepository.save(
                    MatchIgnore.of(proposal.getSenderPost(), proposal.getReceiverPost()));
        }
    }
}
