package com.sookmyung.swapclass.domain.exchange.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CancelRequest {

    @NotBlank(message = "파기 사유는 필수입니다.")
    private String reason; // MUTUAL / FRAUD / MONEY_DEMAND / ABUSE / OTHER

    private String detail; // 기타 사유 상세 (선택)
}
