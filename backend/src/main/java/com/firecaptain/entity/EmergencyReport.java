package com.firecaptain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "emergency_reports")
@Data
@EqualsAndHashCode(callSuper = false)
@EntityListeners(AuditingEntityListener.class)
public class EmergencyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "report_number", unique = true, nullable = false)
    private String reportNumber;

    @Column(name = "caller_name", nullable = false)
    private String callerName;

    @Column(name = "caller_phone", nullable = false)
    private String callerPhone;

    @Column(name = "emergency_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private EmergencyType emergencyType;

    @Column(name = "location_address", nullable = false)
    private String locationAddress;

    @Column(name = "location_latitude")
    private Double locationLatitude;

    @Column(name = "location_longitude")
    private Double locationLongitude;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "priority_level", nullable = false)
    @Enumerated(EnumType.STRING)
    private PriorityLevel priorityLevel;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    @Column(name = "received_at", nullable = false)
    private LocalDateTime receivedAt;

    @Column(name = "dispatched_at")
    private LocalDateTime dispatchedAt;

    @Column(name = "arrived_at")
    private LocalDateTime arrivedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "estimated_duration")
    private Integer estimatedDurationMinutes;

    @Column(name = "actual_duration")
    private Integer actualDurationMinutes;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum EmergencyType {
        FIRE, MEDICAL, TRAFFIC_ACCIDENT, RESCUE, HAZMAT, OTHER
    }

    public enum PriorityLevel {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    public enum ReportStatus {
        RECEIVED, DISPATCHED, EN_ROUTE, ON_SCENE, COMPLETED, CANCELLED
    }

    // 手動でgetter/setterを追加（Lombokの問題を回避）
    public String getReportNumber() {
        return reportNumber;
    }

    public void setReportNumber(String reportNumber) {
        this.reportNumber = reportNumber;
    }

    public EmergencyType getEmergencyType() {
        return emergencyType;
    }

    public void setEmergencyType(EmergencyType emergencyType) {
        this.emergencyType = emergencyType;
    }

    public PriorityLevel getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(PriorityLevel priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }

    public LocalDateTime getDispatchedAt() {
        return dispatchedAt;
    }

    public void setDispatchedAt(LocalDateTime dispatchedAt) {
        this.dispatchedAt = dispatchedAt;
    }

    public LocalDateTime getArrivedAt() {
        return arrivedAt;
    }

    public void setArrivedAt(LocalDateTime arrivedAt) {
        this.arrivedAt = arrivedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
