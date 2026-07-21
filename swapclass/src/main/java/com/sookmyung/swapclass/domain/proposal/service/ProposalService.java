package com.sookmyung.swapclass.domain.proposal.service;

import com.sookmyung.swapclass.domain.proposal.entity.Proposal;
import com.sookmyung.swapclass.domain.proposal.repository.ProposalRepository;
import com.sookmyung.swapclass.global.exception.CustomException;
import com.sookmyung.swapclass.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 교환 제안 도메인 서비스 골격.
 * 실제 유즈케이스(보내기/철회/조회/수락/거절)는 후속 이슈(#2~#5)에서 구현한다.
 * 여기서는 공통 조회/검증 헬퍼만 제공한다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProposalService {

    private final ProposalRepository proposalRepository;

    // ─── 공통 헬퍼 ────────────────────────────────────────────

    // 제안 단건 조회 (없으면 예외)
    protected Proposal getProposalOrThrow(Long proposalId) {
        return proposalRepository.findById(proposalId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROPOSAL_NOT_FOUND));
    }

    // 요청 보낸 본인인지 검증 (철회 등에서 사용)
    protected void validateSender(Proposal proposal, Long userId) {
        if (!proposal.getSender().getId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
    }

    // 요청 받은 본인인지 검증 (수락/거절에서 사용)
    protected void validateReceiver(Proposal proposal, Long userId) {
        if (!proposal.getReceiver().getId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
    }
}
