package com.firecaptain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 緊急通報エンティティ
 * 
 * 消防署への緊急通報情報を管理するエンティティクラスです。
 * 通報の詳細情報、位置情報、優先度、ステータスなどを含みます。
 * 
 * @author FireCaptain Team
 * @version 1.0
 */
@Entity
@Table(name = "emergency_calls")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class EmergencyCall extends BaseEntity {

    /**
     * 通報番号（一意）
     * システム内で通報を識別するための番号
     */
    @Column(name = "call_number", unique = true, nullable = false)
    private String callNumber;

    /**
     * 通報者名
     * 緊急通報を行った人物の名前
     */
    @Column(name = "caller_name")
    private String callerName;

    /**
     * 通報者電話番号
     * 緊急通報を行った人物の連絡先
     */
    @Column(name = "caller_phone")
    private String callerPhone;

    /**
     * 事故事象発生場所
     * 緊急事態が発生した住所
     */
    @Column(name = "incident_address", nullable = false)
    private String incidentAddress;

    /**
     * 緯度
     * 事故事象発生場所の緯度座標
     */
    @Column(name = "latitude")
    private Double latitude;

    /**
     * 経度
     * 事故事象発生場所の経度座標
     */
    @Column(name = "longitude")
    private Double longitude;

    /**
     * 事故事象の詳細説明
     * 通報者が報告した事故事象の詳細情報
     */
    @Column(name = "incident_description", columnDefinition = "TEXT")
    private String incidentDescription;

    /**
     * 事故事象の種類
     * 火災、医療緊急事態、交通事故などの分類
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "incident_type", nullable = false)
    private IncidentType incidentType;

    /**
     * 優先度レベル
     * 緊急度に基づく優先度の分類
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "priority_level", nullable = false)
    private PriorityLevel priorityLevel;

    /**
     * 通報ステータス
     * 通報の処理状況を示すステータス
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CallStatus status = CallStatus.PENDING;

    /**
     * 通報受信時刻
     * 緊急通報を受信した時刻
     */
    @Column(name = "received_at", nullable = false)
    private LocalDateTime receivedAt;

    /**
     * 出動指示時刻
     * 消防隊に出動指示を出した時刻
     */
    @Column(name = "dispatched_at")
    private LocalDateTime dispatchedAt;

    /**
     * 現場到着時刻
     * 消防隊が現場に到着した時刻
     */
    @Column(name = "arrived_at")
    private LocalDateTime arrivedAt;

    /**
     * 現場撤収時刻
     * 消防隊が現場から撤収した時刻
     */
    @Column(name = "cleared_at")
    private LocalDateTime clearedAt;

    /**
     * 担当消防署
     * この通報を担当する消防署
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_station_id")
    private FireStation assignedStation;

    /**
     * 担当消防士
     * この通報に対応する消防士のリスト
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "emergency_call_firefighters", joinColumns = @JoinColumn(name = "emergency_call_id"), inverseJoinColumns = @JoinColumn(name = "firefighter_id"))
    private List<Firefighter> assignedFirefighters;

    /**
     * 通報更新履歴
     * 通報の状況変更履歴
     */
    @OneToMany(mappedBy = "emergencyCall", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CallUpdate> callUpdates;

    /**
     * 事故事象の種類を定義する列挙型
     */
    public enum IncidentType {
        FIRE, // 火災
        MEDICAL_EMERGENCY, // 医療緊急事態
        TRAFFIC_ACCIDENT, // 交通事故
        HAZMAT, // 危険物事故
        RESCUE, // 救助
        FALSE_ALARM, // 誤報
        OTHER // その他
    }

    /**
     * 優先度レベルを定義する列挙型
     */
    public enum PriorityLevel {
        LOW, // 低優先度
        MEDIUM, // 中優先度
        HIGH, // 高優先度
        CRITICAL // 緊急優先度
    }

    /**
     * 通報ステータスを定義する列挙型
     */
    public enum CallStatus {
        PENDING, // 待機中
        DISPATCHED, // 出動指示済み
        EN_ROUTE, // 出動中
        ON_SCENE, // 現場到着
        CLEARED, // 現場撤収
        CANCELLED // キャンセル
    }
}
