package com.firecaptain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;

@Entity
@Table(name = "equipment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Equipment extends BaseEntity {

    @Column(name = "equipment_code", unique = true, nullable = false)
    private String equipmentCode;

    @Column(name = "equipment_name", nullable = false)
    private String equipmentName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "equipment_type", nullable = false)
    private EquipmentType equipmentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EquipmentStatus status = EquipmentStatus.AVAILABLE;

    @Column(name = "manufacturer")
    private String manufacturer;

    @Column(name = "model_number")
    private String modelNumber;

    @Column(name = "serial_number")
    private String serialNumber;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(name = "last_maintenance_date")
    private LocalDate lastMaintenanceDate;

    @Column(name = "next_maintenance_date")
    private LocalDate nextMaintenanceDate;

    @Column(name = "location")
    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fire_station_id")
    private FireStation fireStation;

    public enum EquipmentType {
        FIRE_TRUCK,
        AMBULANCE,
        LADDER_TRUCK,
        PUMPER_TRUCK,
        RESCUE_VEHICLE,
        HAZMAT_VEHICLE,
        PERSONAL_PROTECTIVE_EQUIPMENT,
        FIRE_EXTINGUISHER,
        HOSE,
        LADDER,
        TOOLS,
        COMMUNICATION_EQUIPMENT,
        MEDICAL_EQUIPMENT
    }

    public enum EquipmentStatus {
        AVAILABLE,
        IN_USE,
        MAINTENANCE,
        OUT_OF_SERVICE,
        RETIRED
    }
}
