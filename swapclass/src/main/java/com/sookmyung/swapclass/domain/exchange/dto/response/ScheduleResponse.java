package com.sookmyung.swapclass.domain.exchange.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ScheduleResponse {
    private LocalDateTime scheduledAt;
    private LocalDateTime autoConfirmAt;
}
