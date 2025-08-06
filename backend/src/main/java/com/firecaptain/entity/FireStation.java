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
@Table(name = "fire_stations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class FireStation extends BaseEntity {

    @Column(name = "station_code", unique = true, nullable = false)
    private String stationCode;

    @Column(name = "station_name", nullable = false)
    private String stationName;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "station_type")
    private StationType stationType;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @OneToMany(mappedBy = "fireStation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Firefighter> firefighters;

    @OneToMany(mappedBy = "fireStation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Equipment> equipment;

    public enum StationType {
        MAIN_STATION,
        BRANCH_STATION,
        SUB_STATION,
        MOBILE_UNIT
    }
}
