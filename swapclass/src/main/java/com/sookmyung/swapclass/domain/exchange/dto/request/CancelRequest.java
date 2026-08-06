package com.sookmyung.swapclass.domain.exchange.dto.request;

import com.sookmyung.swapclass.domain.exchange.entity.CancelReason;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CancelRequest {

    @NotNull(message = "파기 사유는 필수입니다.")
    private CancelReason reason;

    private String detail; // 기타 사유 상세 (선택)
}