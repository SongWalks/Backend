package com.sookmyung.swapclass.domain.report.repository;

import com.sookmyung.swapclass.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    // 신고자 기준 신고 목록
    List<Report> findByReporterIdOrderByCreatedAtDesc(Long reporterId);

    // 피신고자 기준 신고 누적 수
    long countByReportedUserId(Long reportedUserId);
}