package com.sookmyung.swapclass.domain.inquiry.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class InquiryRequest {

    @NotBlank(message = "문의 내용은 필수입니다.")
    private String content;

    private Long relatedReportId; // 선택
}
