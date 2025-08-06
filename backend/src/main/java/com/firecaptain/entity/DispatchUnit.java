package com.firecaptain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "dispatch_units")
@Data
@EqualsAndHashCode(callSuper = false)
@EntityListeners(AuditingEntityListener.class)
public class DispatchUnit {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispatch_id", nullable = false)
    private Dispatch dispatch;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private UnitStatus status;
    
    @Column(name = "dispatched_at", nullable = false)
    private LocalDateTime dispatchedAt;
    
    @Column(name = "en_route_at")
    private LocalDateTime enRouteAt;
    
    @Column(name = "arrived_at")
    private LocalDateTime arrivedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "estimated_arrival_time")
    private LocalDateTime estimatedArrivalTime;
    
    @Column(name = "actual_arrival_time")
    private LocalDateTime actualArrivalTime;
    
    @Column(name = "response_time_minutes")
    private Integer responseTimeMinutes;
    
    @Column(name = "travel_distance_km")
    private Double travelDistanceKm;
    
    @Column(name = "travel_time_minutes")
    private Integer travelTimeMinutes;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    public enum UnitStatus {
        DISPATCHED, EN_ROUTE, ON_SCENE, COMPLETED, CANCELLED, UNAVAILABLE
    }
}
