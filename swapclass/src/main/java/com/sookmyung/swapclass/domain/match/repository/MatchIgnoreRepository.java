package com.sookmyung.swapclass.domain.match.repository;

import com.sookmyung.swapclass.domain.match.entity.MatchIgnore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchIgnoreRepository extends JpaRepository<MatchIgnore, Long> {

    // 정규화된 쌍(작은 id = a, 큰 id = b)으로 제외 여부 확인
    boolean existsByPostAIdAndPostBId(Long postAId, Long postBId);
}
