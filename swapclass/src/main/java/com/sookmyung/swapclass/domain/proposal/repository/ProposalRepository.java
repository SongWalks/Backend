package com.sookmyung.swapclass.domain.proposal.repository;

import com.sookmyung.swapclass.domain.proposal.entity.Proposal;
import com.sookmyung.swapclass.domain.proposal.entity.ProposalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProposalRepository extends JpaRepository<Proposal, Long> {

    // 만료 대상 조회 (ProposalExpireJob용): 지정 상태이고 만료 시각이 지난 요청
    List<Proposal> findByStatusAndExpiresAtBefore(ProposalStatus status, LocalDateTime time);

    // 내가 보낸 특정 상태의 요청 단건 (동시 1개 제한 → PENDING 조회)
    Optional<Proposal> findBySenderIdAndStatus(Long senderId, ProposalStatus status);

    // 내가 보낸 진행 중 요청 존재 여부 (제안 보내기 409 판단용)
    boolean existsBySenderIdAndStatus(Long senderId, ProposalStatus status);

    // 내가 보낸 가장 최근 요청 (보낸 제안 조회: 상태 무관 최신 1건)
    Optional<Proposal> findFirstBySenderIdOrderByCreatedAtDesc(Long senderId);

    // 동일 게시글 쌍에 이미 보낸 요청 존재 여부 (candidates isAlreadyRequested용)
    boolean existsBySenderPostIdAndReceiverPostIdAndStatus(
            Long senderPostId, Long receiverPostId, ProposalStatus status);

    // 내가 받은 특정 상태의 요청 목록, 만료 임박순 (받은 제안 목록: expires_at ASC)
    List<Proposal> findByReceiverIdAndStatusOrderByExpiresAtAsc(Long receiverId, ProposalStatus status);

    // 특정 게시글에 들어온 특정 상태의 요청 목록 (수락 시 나머지 일괄 거절용)
    List<Proposal> findByReceiverPostIdAndStatus(Long receiverPostId, ProposalStatus status);
}
