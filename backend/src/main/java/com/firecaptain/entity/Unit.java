package com.firecaptain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * 消防車両・ユニットエンティティ
 * 
 * 消防車両や救急車などの出動ユニットを表すエンティティです。
 * 各ユニットは特定の消防署に所属し、出動可能な状態を管理します。
 * 
 * @author FireCaptain Team
 * @version 1.0
 */
@Entity
@Table(name = "units")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Unit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** ユニット番号（例：消防車1号） */
    @Column(name = "unit_number", unique = true, nullable = false)
    private String unitNumber;

    /** ユニット名 */
    @Column(name = "unit_name", nullable = false)
    private String unitName;

    /** ユニットタイプ（消防車、救急車、はしご車など） */
    @Enumerated(EnumType.STRING)
    @Column(name = "unit_type", nullable = false)
    private UnitType unitType;

    /** 所属消防署 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fire_station_id", nullable = false)
    private FireStation fireStation;

    /** 現在のステータス */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UnitStatus status = UnitStatus.AVAILABLE;

    /** 現在地の緯度 */
    @Column(name = "current_latitude")
    private Double currentLatitude;

    /** 現在地の経度 */
    @Column(name = "current_longitude")
    private Double currentLongitude;

    /** 最後の位置更新時刻 */
    @Column(name = "last_location_update")
    private LocalDateTime lastLocationUpdate;

    /** 乗員数 */
    @Column(name = "crew_count")
    private Integer crewCount;

    /** 最大乗員数 */
    @Column(name = "max_crew_count")
    private Integer maxCrewCount;

    /** 装備品情報（JSON形式） */
    @Column(name = "equipment_info", columnDefinition = "TEXT")
    private String equipmentInfo;

    /** メンテナンス状況 */
    @Enumerated(EnumType.STRING)
    @Column(name = "maintenance_status")
    private MaintenanceStatus maintenanceStatus = MaintenanceStatus.OPERATIONAL;

    /** 作成日時 */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /** 更新日時 */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** 備考 */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * ユニットタイプの列挙型
     */
    public enum UnitType {
        FIRE_ENGINE, // 消防車
        AMBULANCE, // 救急車
        LADDER_TRUCK, // はしご車
        RESCUE_VEHICLE, // 救助車
        COMMAND_VEHICLE, // 指揮車
        WATER_TANKER, // 水槽車
        FOAM_TRUCK, // 泡消火車
        SPECIAL_UNIT // 特殊車両
    }

    /**
     * ユニットステータスの列挙型
     */
    public enum UnitStatus {
        AVAILABLE, // 待機中
        DISPATCHED, // 出動中
        ON_SCENE, // 現場到着
        RETURNING, // 帰署中
        MAINTENANCE, // メンテナンス中
        OUT_OF_SERVICE // 運用停止
    }

    /**
     * メンテナンス状況の列挙型
     */
    public enum MaintenanceStatus {
        OPERATIONAL, // 運用可能
        MINOR_ISSUE, // 軽微な問題
        MAJOR_ISSUE, // 重大な問題
        UNDER_REPAIR // 修理中
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // 手動でgetter/setterを追加（Lombokの問題を回避）
    public String getUnitNumber() {
        return unitNumber;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }
}
