package com.firecaptain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;

@Entity
@Table(name = "certifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Certification extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "firefighter_id", nullable = false)
    private Firefighter firefighter;

    @Column(name = "certification_name", nullable = false)
    private String certificationName;

    @Column(name = "issuing_authority", nullable = false)
    private String issuingAuthority;

    @Column(name = "certification_number")
    private String certificationNumber;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "certification_type", nullable = false)
    private CertificationType certificationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CertificationStatus status = CertificationStatus.ACTIVE;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public enum CertificationType {
        FIREFIGHTER_I,
        FIREFIGHTER_II,
        HAZMAT_OPERATIONS,
        HAZMAT_TECHNICIAN,
        RESCUE_TECHNICIAN,
        EMERGENCY_MEDICAL_TECHNICIAN,
        PARAMEDIC,
        DRIVER_OPERATOR,
        OFFICER,
        INSTRUCTOR,
        INSPECTOR,
        INVESTIGATOR
    }

    public enum CertificationStatus {
        ACTIVE,
        EXPIRED,
        SUSPENDED,
        REVOKED
    }
}
