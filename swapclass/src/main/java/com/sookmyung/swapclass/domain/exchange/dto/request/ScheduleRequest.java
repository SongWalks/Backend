package com.sookmyung.swapclass.domain.exchange.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
public class ScheduleRequest {

    // 프론트는 UTC(끝에 Z) 형식으로 절대시각을 보낸다. OffsetDateTime으로 받아야
    // 타임존 정보(Z)를 유지한 채 서버에서 KST로 정확히 변환할 수 있다.
    // (LocalDateTime으로 받으면 Z가 버려져 UTC 값을 KST로 오인 저장 → 9시간 오차)
    @NotNull(message = "교환 시간은 필수입니다.")
    @Future(message = "교환 시간은 현재 이후여야 합니다.")
    private OffsetDateTime scheduledAt;
}
