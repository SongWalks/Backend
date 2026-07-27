package com.sookmyung.swapclass.domain.admin.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SanctionRequest {

    @NotBlank(message = "제재 유형은 필수입니다.")
    private String type; // REPORT / FALSE_REPORT / TIMEOUT

    @NotBlank(message = "제재 사유는 필수입니다.")
    private String reason;

    @NotBlank(message = "정지 기간은 필수입니다.")
    private String suspendDays; // 3 / semester / permanent
}
