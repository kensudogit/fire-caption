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
@Table(name = "data_analyses")
@Data
@EqualsAndHashCode(callSuper = false)
@EntityListeners(AuditingEntityListener.class)
public class DataAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "analysis_id", unique = true, nullable = false)
    private String analysisId;

    @Column(name = "analysis_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AnalysisType analysisType;

    @Column(name = "time_period", nullable = false)
    @Enumerated(EnumType.STRING)
    private TimePeriod timePeriod;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private AnalysisStatus status;

    @Column(name = "total_incidents")
    private Integer totalIncidents;

    @Column(name = "average_response_time_minutes")
    private Double averageResponseTimeMinutes;

    @Column(name = "average_travel_time_minutes")
    private Double averageTravelTimeMinutes;

    @Column(name = "average_scene_time_minutes")
    private Double averageSceneTimeMinutes;

    @Column(name = "total_casualties")
    private Integer totalCasualties;

    @Column(name = "total_fatalities")
    private Integer totalFatalities;

    @Column(name = "total_injuries")
    private Integer totalInjuries;

    @Column(name = "total_rescued")
    private Integer totalRescued;

    @Column(name = "total_property_damage")
    private Double totalPropertyDamage;

    @Column(name = "total_operational_cost")
    private Double totalOperationalCost;

    @Column(name = "peak_hours", columnDefinition = "TEXT")
    private String peakHours;

    @Column(name = "hotspots", columnDefinition = "TEXT")
    private String hotspots;

    @Column(name = "trends", columnDefinition = "TEXT")
    private String trends;

    @Column(name = "patterns", columnDefinition = "TEXT")
    private String patterns;

    @Column(name = "recommendations", columnDefinition = "TEXT")
    private String recommendations;

    @Column(name = "insights", columnDefinition = "TEXT")
    private String insights;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    @Column(name = "generated_by")
    private String generatedBy;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @OneToMany(mappedBy = "dataAnalysis", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AnalysisMetric> metrics;

    @OneToMany(mappedBy = "dataAnalysis", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AnalysisChart> charts;

    @OneToMany(mappedBy = "dataAnalysis", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AnalysisReport> reports;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum AnalysisType {
        RESPONSE_TIME, INCIDENT_VOLUME, GEOGRAPHIC_DISTRIBUTION, RESOURCE_UTILIZATION,
        COST_ANALYSIS, PERFORMANCE_METRICS, TREND_ANALYSIS, PREDICTIVE_MODELING
    }

    public enum TimePeriod {
        DAILY, WEEKLY, MONTHLY, QUARTERLY, YEARLY, CUSTOM
    }

    public enum AnalysisStatus {
        PENDING, IN_PROGRESS, COMPLETED, FAILED, ARCHIVED
    }
}
