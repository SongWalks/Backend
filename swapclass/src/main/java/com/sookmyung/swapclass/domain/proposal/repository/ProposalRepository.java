package com.sookmyung.swapclass.domain.proposal.repository;

import com.sookmyung.swapclass.domain.proposal.entity.Proposal;
import com.sookmyung.swapclass.domain.proposal.entity.ProposalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ProposalRepository extends JpaRepository<Proposal, Long> {

    // 만료 대상 조회 (ProposalExpireJob용): 지정 상태이고 만료 시각이 지난 요청
    List<Proposal> findByStatusAndExpiresAtBefore(ProposalStatus status, LocalDateTime time);
}
