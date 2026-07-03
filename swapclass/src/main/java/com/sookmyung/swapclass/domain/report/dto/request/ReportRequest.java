package com.sookmyung.swapclass.domain.report.dto.request;

import com.sookmyung.swapclass.domain.report.entity.ReportReason;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class ReportRequest {

    @NotNull(message = "피신고자 ID는 필수입니다.")
    private Long reportedUserId;

    @NotNull(message = "신고 사유는 필수입니다.")
    private ReportReason reason;

    @NotEmpty(message = "증거 사진을 첨부해주세요.")
    private List<String> imageUrls;

    private Long exchangeId; // 선택
}