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
@Table(name = "dispatches")
@Data
@EqualsAndHashCode(callSuper = false)
@EntityListeners(AuditingEntityListener.class)
public class Dispatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dispatch_number", unique = true, nullable = false)
    private String dispatchNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emergency_report_id", nullable = false)
    private EmergencyReport emergencyReport;

    @Column(name = "dispatch_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private DispatchType dispatchType;

    @Column(name = "priority_level", nullable = false)
    @Enumerated(EnumType.STRING)
    private EmergencyReport.PriorityLevel priorityLevel;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private DispatchStatus status;

    @Column(name = "dispatched_at", nullable = false)
    private LocalDateTime dispatchedAt;

    @Column(name = "estimated_arrival_time")
    private LocalDateTime estimatedArrivalTime;

    @Column(name = "actual_arrival_time")
    private LocalDateTime actualArrivalTime;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "dispatch", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DispatchUnit> dispatchUnits;

    @OneToMany(mappedBy = "dispatch", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DispatchUpdate> updates;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum DispatchType {
        FIRE_ENGINE, AMBULANCE, LADDER_TRUCK, RESCUE_UNIT, HAZMAT_UNIT, COMMAND_UNIT
    }

    public enum DispatchStatus {
        DISPATCHED, EN_ROUTE, ON_SCENE, COMPLETED, CANCELLED
    }

    // 手動でgetter/setterを追加（Lombokの問題を回避）
    public String getDispatchNumber() {
        return dispatchNumber;
    }

    public void setDispatchNumber(String dispatchNumber) {
        this.dispatchNumber = dispatchNumber;
    }

    public EmergencyReport getEmergencyReport() {
        return emergencyReport;
    }

    public void setEmergencyReport(EmergencyReport emergencyReport) {
        this.emergencyReport = emergencyReport;
    }

    public DispatchType getDispatchType() {
        return dispatchType;
    }

    public void setDispatchType(DispatchType dispatchType) {
        this.dispatchType = dispatchType;
    }

    public EmergencyReport.PriorityLevel getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(EmergencyReport.PriorityLevel priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public DispatchStatus getStatus() {
        return status;
    }

    public void setStatus(DispatchStatus status) {
        this.status = status;
    }

    public LocalDateTime getDispatchedAt() {
        return dispatchedAt;
    }

    public void setDispatchedAt(LocalDateTime dispatchedAt) {
        this.dispatchedAt = dispatchedAt;
    }

    public LocalDateTime getActualArrivalTime() {
        return actualArrivalTime;
    }

    public void setActualArrivalTime(LocalDateTime actualArrivalTime) {
        this.actualArrivalTime = actualArrivalTime;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
