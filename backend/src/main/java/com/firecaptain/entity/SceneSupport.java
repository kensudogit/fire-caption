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
@Table(name = "scene_supports")
@Data
@EqualsAndHashCode(callSuper = false)
@EntityListeners(AuditingEntityListener.class)
public class SceneSupport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispatch_id", nullable = false)
    private Dispatch dispatch;

    @Column(name = "support_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private SupportType supportType;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private SupportStatus status;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "dispatched_at")
    private LocalDateTime dispatchedAt;

    @Column(name = "arrived_at")
    private LocalDateTime arrivedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "priority_level", nullable = false)
    @Enumerated(EnumType.STRING)
    private EmergencyReport.PriorityLevel priorityLevel;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "estimated_duration_minutes")
    private Integer estimatedDurationMinutes;

    @Column(name = "actual_duration_minutes")
    private Integer actualDurationMinutes;

    @Column(name = "resources_used", columnDefinition = "TEXT")
    private String resourcesUsed;

    @Column(name = "cost_estimate")
    private Double costEstimate;

    @Column(name = "actual_cost")
    private Double actualCost;

    @OneToMany(mappedBy = "sceneSupport", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SupportResource> resources;

    @OneToMany(mappedBy = "sceneSupport", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SupportUpdate> updates;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum SupportType {
        ADDITIONAL_UNITS, SPECIALIZED_EQUIPMENT, PERSONNEL_REINFORCEMENT,
        MEDICAL_SUPPORT, TECHNICAL_SUPPORT, LOGISTICS_SUPPORT
    }

    public enum SupportStatus {
        REQUESTED, APPROVED, DISPATCHED, ON_SCENE, COMPLETED, CANCELLED
    }
}
