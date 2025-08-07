package com.firecaptain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * 消防署エンティティ
 * 
 * 消防署の基本情報を管理するエンティティです。
 * 消防署の位置、連絡先、管轄区域などの情報を含みます。
 * 
 * @author FireCaptain Team
 * @version 1.0
 */
@Entity
@Table(name = "fire_stations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FireStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 消防署名 */
    @Column(name = "station_name", nullable = false)
    private String stationName;

    /** 消防署コード */
    @Column(name = "station_code", unique = true, nullable = false)
    private String stationCode;

    /** 住所 */
    @Column(name = "address", nullable = false)
    private String address;

    /** 緯度 */
    @Column(name = "latitude")
    private Double latitude;

    /** 経度 */
    @Column(name = "longitude")
    private Double longitude;

    /** 電話番号 */
    @Column(name = "phone_number")
    private String phoneNumber;

    /** 管轄区域 */
    @Column(name = "jurisdiction_area", columnDefinition = "TEXT")
    private String jurisdictionArea;

    /** 作成日時 */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /** 消防署タイプ */
    @Enumerated(EnumType.STRING)
    @Column(name = "station_type")
    private StationType stationType = StationType.MAIN_STATION;

    /** アクティブ状態 */
    @Column(name = "is_active")
    private Boolean isActive = true;

    /** 収容能力 */
    @Column(name = "capacity")
    private Integer capacity;

    /** 更新日時 */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 消防署タイプの列挙型
     */
    public enum StationType {
        MAIN_STATION, // 本署
        BRANCH_STATION, // 分署
        SUB_STATION, // 出張所
        MOBILE_UNIT // 移動隊
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
}
