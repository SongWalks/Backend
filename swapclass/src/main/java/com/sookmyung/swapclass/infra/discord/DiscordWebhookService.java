package com.sookmyung.swapclass.infra.discord;

import com.sookmyung.swapclass.domain.report.dto.request.ReportRequest;
import com.sookmyung.swapclass.domain.report.entity.Report;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DiscordWebhookService {

    @Value("${discord.webhook.url}")
    private String webhookUrl;

    private final RestTemplate restTemplate;

    public void sendReportNotification(Report report) {
        String content = String.format(
                "🚨 **신고 접수**\n" +
                        "- 신고자 ID: %d\n" +
                        "- 피신고자 ID: %d\n" +
                        "- 사유: %s\n" +
                        "- 증거 사진: %s\n" +
                        "- 교환 ID: %s",
                report.getReporter().getId(),
                report.getReportedUser().getId(),
                report.getReason().name(),
                report.getImageUrls(),
                report.getExchangeId() != null ? report.getExchangeId().toString() : "없음"
        );

        Map<String, Object> body = new HashMap<>();
        body.put("content", content);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity(webhookUrl, entity, String.class);
        } catch (Exception e) {
            // Webhook 실패해도 신고 접수는 정상 처리
        }
    }
}