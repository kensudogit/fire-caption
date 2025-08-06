package com.firecaptain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "emergency_calls")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class EmergencyCall extends BaseEntity {

    @Column(name = "call_number", unique = true, nullable = false)
    private String callNumber;

    @Column(name = "caller_name")
    private String callerName;

    @Column(name = "caller_phone")
    private String callerPhone;

    @Column(name = "incident_address", nullable = false)
    private String incidentAddress;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "incident_description", columnDefinition = "TEXT")
    private String incidentDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "incident_type", nullable = false)
    private IncidentType incidentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority_level", nullable = false)
    private PriorityLevel priorityLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CallStatus status = CallStatus.PENDING;

    @Column(name = "received_at", nullable = false)
    private LocalDateTime receivedAt;

    @Column(name = "dispatched_at")
    private LocalDateTime dispatchedAt;

    @Column(name = "arrived_at")
    private LocalDateTime arrivedAt;

    @Column(name = "cleared_at")
    private LocalDateTime clearedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_station_id")
    private FireStation assignedStation;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "emergency_call_firefighters", joinColumns = @JoinColumn(name = "emergency_call_id"), inverseJoinColumns = @JoinColumn(name = "firefighter_id"))
    private List<Firefighter> assignedFirefighters;

    @OneToMany(mappedBy = "emergencyCall", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CallUpdate> callUpdates;

    public enum IncidentType {
        FIRE,
        MEDICAL_EMERGENCY,
        TRAFFIC_ACCIDENT,
        HAZMAT,
        RESCUE,
        FALSE_ALARM,
        OTHER
    }

    public enum PriorityLevel {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }

    public enum CallStatus {
        PENDING,
        DISPATCHED,
        EN_ROUTE,
        ON_SCENE,
        CLEARED,
        CANCELLED
    }
}
