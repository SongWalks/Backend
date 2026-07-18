package com.sookmyung.swapclass.domain.scheduler;

import com.sookmyung.swapclass.domain.notification.service.NotificationService;
import com.sookmyung.swapclass.domain.proposal.entity.Proposal;
import com.sookmyung.swapclass.domain.proposal.entity.ProposalStatus;
import com.sookmyung.swapclass.domain.proposal.repository.ProposalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProposalExpireJob {

    private final ProposalRepository proposalRepository;
    private final NotificationService notificationService;

    // 1분마다 실행 — PENDING 상태이고 만료 시간이 지난 요청 자동 만료
    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void expireProposals() {
        List<Proposal> expiredProposals = proposalRepository
                .findByStatusAndExpiresAtBefore(ProposalStatus.PENDING, LocalDateTime.now());

        for (Proposal proposal : expiredProposals) {
            proposal.markExpired();

            // 발신자에게 타임아웃 알림
            notificationService.sendMatchTimeoutNotification(proposal.getSender());

            log.info("ProposalExpireJob - proposalId: {} 만료 처리", proposal.getId());
        }

        if (!expiredProposals.isEmpty()) {
            log.info("ProposalExpireJob - 총 {}건 만료 처리 완료", expiredProposals.size());
        }
    }
}
