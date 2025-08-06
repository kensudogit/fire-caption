package com.firecaptain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Permission extends BaseEntity {

    @Column(name = "permission_name", unique = true, nullable = false)
    private String permissionName;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "resource", nullable = false)
    private Resource resource;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    private Action action;

    public enum Resource {
        EMERGENCY_CALLS,
        FIREFIGHTERS,
        FIRE_STATIONS,
        EQUIPMENT,
        USERS,
        REPORTS,
        DASHBOARD,
        MAPS,
        SETTINGS
    }

    public enum Action {
        CREATE,
        READ,
        UPDATE,
        DELETE,
        DISPATCH,
        APPROVE,
        EXPORT,
        IMPORT
    }
}
