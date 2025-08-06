package com.firecaptain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "call_updates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class CallUpdate extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emergency_call_id", nullable = false)
    private EmergencyCall emergencyCall;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "firefighter_id", nullable = false)
    private Firefighter firefighter;

    @Column(name = "update_text", columnDefinition = "TEXT", nullable = false)
    private String updateText;

    @Enumerated(EnumType.STRING)
    @Column(name = "update_type", nullable = false)
    private UpdateType updateType;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "location_latitude")
    private Double locationLatitude;

    @Column(name = "location_longitude")
    private Double locationLongitude;

    public enum UpdateType {
        STATUS_CHANGE,
        LOCATION_UPDATE,
        RESOURCE_REQUEST,
        SITUATION_REPORT,
        CLEARANCE_REPORT,
        GENERAL_UPDATE
    }
}
