package com.sookmyung.swapclass.domain.exchange.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ScheduleRequest {

    @NotNull(message = "교환 시간은 필수입니다.")
    private LocalDateTime scheduledAt;
}
