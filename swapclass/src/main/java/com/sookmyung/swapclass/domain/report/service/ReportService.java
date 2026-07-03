package com.sookmyung.swapclass.domain.report.service;

import com.sookmyung.swapclass.domain.report.dto.request.ReportRequest;
import com.sookmyung.swapclass.domain.report.entity.Report;
import com.sookmyung.swapclass.domain.report.repository.ReportRepository;
import com.sookmyung.swapclass.domain.user.entity.User;
import com.sookmyung.swapclass.domain.user.repository.UserRepository;
import com.sookmyung.swapclass.global.exception.CustomException;
import com.sookmyung.swapclass.global.exception.ErrorCode;
import com.sookmyung.swapclass.infra.discord.DiscordWebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final DiscordWebhookService discordWebhookService;

    @Transactional
    public Long createReport(Long reporterId, ReportRequest request) {
        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        User reportedUser = userRepository.findById(request.getReportedUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Report report = Report.builder()
                .reporter(reporter)
                .reportedUser(reportedUser)
                .reason(request.getReason())
                .imageUrls(String.join(",", request.getImageUrls()))
                .exchangeId(request.getExchangeId())
                .build();

        reportRepository.save(report);

        // Discord Webhook 발송
        discordWebhookService.sendReportNotification(report);

        return report.getId();
    }
}