package com.sookmyung.swapclass.domain.exchange.repository;

import com.sookmyung.swapclass.domain.exchange.entity.Exchange;
import com.sookmyung.swapclass.domain.exchange.entity.ExchangeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ExchangeRepository extends JpaRepository<Exchange, Long> {

    // 자동 완료 대상 조회 (AutoConfirmJob용)
    List<Exchange> findByStatusAndAutoConfirmAtBefore(
            ExchangeStatus status, LocalDateTime now);
}
