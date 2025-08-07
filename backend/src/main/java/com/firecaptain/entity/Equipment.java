package com.firecaptain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;

/**
 * 装備品エンティティ
 * 
 * 消防署の装備品情報を管理するエンティティクラスです。
 * 消防車両、救急車、消防器具、個人防護具などの
 * 装備品の詳細情報とメンテナンス履歴を含みます。
 * 
 * @author FireCaptain Team
 * @version 1.0
 */
@Entity
@Table(name = "equipment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Equipment extends BaseEntity {

    /**
     * 装備品コード（一意）
     * 装備品を識別するための一意のコード
     */
    @Column(name = "equipment_code", unique = true, nullable = false)
    private String equipmentCode;

    /**
     * 装備品名
     * 装備品の正式名称
     */
    @Column(name = "equipment_name", nullable = false)
    private String equipmentName;

    /**
     * 説明
     * 装備品の詳細説明
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * 装備品タイプ
     * 装備品の種類（消防車、救急車など）
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "equipment_type", nullable = false)
    private EquipmentType equipmentType;

    /**
     * 装備品ステータス
     * 装備品の現在の状態（利用可能、使用中など）
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EquipmentStatus status = EquipmentStatus.AVAILABLE;

    /**
     * 製造元
     * 装備品の製造会社名
     */
    @Column(name = "manufacturer")
    private String manufacturer;

    /**
     * モデル番号
     * 装備品のモデル番号
     */
    @Column(name = "model_number")
    private String modelNumber;

    /**
     * シリアル番号
     * 装備品のシリアル番号
     */
    @Column(name = "serial_number")
    private String serialNumber;

    /**
     * 購入日
     * 装備品の購入日
     */
    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    /**
     * 最終メンテナンス日
     * 最後にメンテナンスを行った日
     */
    @Column(name = "last_maintenance_date")
    private LocalDate lastMaintenanceDate;

    /**
     * 次回メンテナンス予定日
     * 次回メンテナンスの予定日
     */
    @Column(name = "next_maintenance_date")
    private LocalDate nextMaintenanceDate;

    /**
     * 保管場所
     * 装備品の保管場所
     */
    @Column(name = "location")
    private String location;

    /**
     * 所属消防署
     * この装備品が配備されている消防署
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fire_station_id")
    private FireStation fireStation;

    /**
     * 装備品の種類を定義する列挙型
     */
    public enum EquipmentType {
        FIRE_TRUCK, // 消防車
        AMBULANCE, // 救急車
        LADDER_TRUCK, // はしご車
        PUMPER_TRUCK, // ポンプ車
        RESCUE_VEHICLE, // 救助工作車
        HAZMAT_VEHICLE, // 化学消防車
        PERSONAL_PROTECTIVE_EQUIPMENT, // 個人防護具
        FIRE_EXTINGUISHER, // 消火器
        HOSE, // ホース
        LADDER, // はしご
        TOOLS, // 工具
        COMMUNICATION_EQUIPMENT, // 通信機器
        MEDICAL_EQUIPMENT // 医療機器
    }

    /**
     * 装備品のステータスを定義する列挙型
     */
    public enum EquipmentStatus {
        AVAILABLE, // 利用可能
        IN_USE, // 使用中
        MAINTENANCE, // メンテナンス中
        OUT_OF_SERVICE, // 故障中
        RETIRED // 廃止
    }
}
