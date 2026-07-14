package com.sookmyung.swapclass.domain.exchange.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResultResponse {
    private String exchangeStatus;
    private String message;
}
