package com.firecaptain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "firefighters")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Firefighter extends BaseEntity {

    @Column(name = "employee_id", unique = true, nullable = false)
    private String employeeId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "rank")
    private Rank rank;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status = Status.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fire_station_id")
    private FireStation fireStation;

    @OneToMany(mappedBy = "firefighter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EmergencyCall> emergencyCalls;

    @OneToMany(mappedBy = "firefighter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Certification> certifications;

    public enum Rank {
        FIREFIGHTER,
        SENIOR_FIREFIGHTER,
        LIEUTENANT,
        CAPTAIN,
        BATTALION_CHIEF,
        DEPUTY_CHIEF,
        FIRE_CHIEF
    }

    public enum Status {
        ACTIVE,
        ON_LEAVE,
        RETIRED,
        SUSPENDED
    }
}
