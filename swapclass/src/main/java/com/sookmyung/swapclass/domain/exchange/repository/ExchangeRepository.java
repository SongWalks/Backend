package com.sookmyung.swapclass.domain.exchange.repository;

import com.sookmyung.swapclass.domain.exchange.entity.Exchange;
import com.sookmyung.swapclass.domain.exchange.entity.ExchangeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ExchangeRepository extends JpaRepository<Exchange, Long> {

    // 성사된 제안으로 교환 조회 (제안 → 채팅방 연결용)
    Optional<Exchange> findByProposalId(Long proposalId);

    // 자동 완료 대상 조회 (AutoConfirmJob용)
    List<Exchange> findByStatusAndAutoConfirmAtBefore(
            ExchangeStatus status, LocalDateTime now);

    // 내가 참여(양측 게시글 작성자)한 교환 중 지정 상태가 하나라도 있는지 (탈퇴 가능 여부 체크용)
    @Query("select count(e) > 0 from Exchange e " +
            "where (e.postA.user.id = :userId or e.postB.user.id = :userId) " +
            "and e.status in :statuses")
    boolean existsActiveByUser(@Param("userId") Long userId,
                               @Param("statuses") Collection<ExchangeStatus> statuses);
}
