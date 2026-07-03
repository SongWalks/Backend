package com.sookmyung.swapclass.domain.notification.entity;

public class Report {
    
package com.sookmyung.swapclass.domain.report.entity;

import com.sookmyung.swapclass.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_user_id", nullable = false)
    private User reportedUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportReason reason;

    @Column(name = "image_urls")
    private String imageUrls; // JSON 배열로 저장

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status;

    @Column(name = "exchange_id")
    private Long exchangeId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Report(User reporter, User reportedUser, ReportReason reason,
                  String imageUrls, Long exchangeId) {
        this.reporter = reporter;
        this.reportedUser = reportedUser;
        this.reason = reason;
        this.imageUrls = imageUrls;
        this.exchangeId = exchangeId;
        this.status = ReportStatus.RECEIVED;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void resolve() {
        this.status = ReportStatus.RESOLVED;
    }
}
