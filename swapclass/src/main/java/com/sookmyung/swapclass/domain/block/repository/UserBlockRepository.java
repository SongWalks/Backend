package com.sookmyung.swapclass.domain.block.repository;

import com.sookmyung.swapclass.domain.block.entity.UserBlock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserBlockRepository extends JpaRepository<UserBlock, Long> {

    // 차단 여부 확인
    boolean existsByBlockerIdAndBlockedId(Long blockerId, Long blockedId);

    // 차단 관계 조회 (해제용)
    Optional<UserBlock> findByBlockerIdAndBlockedId(Long blockerId, Long blockedId);
}
