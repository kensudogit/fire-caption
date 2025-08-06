package com.firecaptain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "completion_reports")
@Data
@EqualsAndHashCode(callSuper = false)
@EntityListeners(AuditingEntityListener.class)
public class CompletionReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispatch_id", nullable = false)
    private Dispatch dispatch;

    @Column(name = "report_number", unique = true, nullable = false)
    private String reportNumber;

    @Column(name = "completion_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private CompletionType completionType;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    @Column(name = "completed_at", nullable = false)
    private LocalDateTime completedAt;

    @Column(name = "total_duration_minutes", nullable = false)
    private Integer totalDurationMinutes;

    @Column(name = "response_time_minutes")
    private Integer responseTimeMinutes;

    @Column(name = "travel_time_minutes")
    private Integer travelTimeMinutes;

    @Column(name = "scene_time_minutes")
    private Integer sceneTimeMinutes;

    @Column(name = "casualties_count")
    private Integer casualtiesCount;

    @Column(name = "fatalities_count")
    private Integer fatalitiesCount;

    @Column(name = "injuries_count")
    private Integer injuriesCount;

    @Column(name = "rescued_count")
    private Integer rescuedCount;

    @Column(name = "property_damage_estimate")
    private Double propertyDamageEstimate;

    @Column(name = "resources_used", columnDefinition = "TEXT")
    private String resourcesUsed;

    @Column(name = "total_cost")
    private Double totalCost;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name = "lessons_learned", columnDefinition = "TEXT")
    private String lessonsLearned;

    @Column(name = "recommendations", columnDefinition = "TEXT")
    private String recommendations;

    @Column(name = "weather_conditions")
    private String weatherConditions;

    @Column(name = "traffic_conditions")
    private String trafficConditions;

    @Column(name = "submitted_by")
    private String submittedBy;

    @Column(name = "reviewed_by")
    private String reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @OneToMany(mappedBy = "completionReport", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CompletionAttachment> attachments;

    @OneToMany(mappedBy = "completionReport", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CompletionSignature> signatures;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum CompletionType {
        SUCCESSFUL_RESOLUTION, PARTIAL_RESOLUTION, ESCALATION, TRANSFER, CANCELLATION
    }

    public enum ReportStatus {
        DRAFT, SUBMITTED, REVIEWED, APPROVED, REJECTED
    }
}
