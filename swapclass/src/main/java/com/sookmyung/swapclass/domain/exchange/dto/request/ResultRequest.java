package com.sookmyung.swapclass.domain.exchange.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ResultRequest {

    @NotNull(message = "결과 선택은 필수입니다.")
    private Boolean success; // true = SUCCESS, false = FAIL
}
