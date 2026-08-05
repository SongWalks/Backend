package com.sookmyung.swapclass.domain.exchange.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.LocalDateTime;

@Getter
public class ScheduleResponse {
    private ZonedDateTime scheduledAt;
    private ZonedDateTime autoConfirmAt;

    public ScheduleResponse(LocalDateTime scheduledAt, LocalDateTime autoConfirmAt) {
        this.scheduledAt = scheduledAt.atZone(ZoneOffset.UTC);
        this.autoConfirmAt = autoConfirmAt.atZone(ZoneOffset.UTC);
    }
}
