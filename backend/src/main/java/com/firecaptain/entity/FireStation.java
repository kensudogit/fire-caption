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
 * 消防署エンティティ
 * 
 * 消防署の基本情報を管理するエンティティクラスです。
 * 消防署の位置情報、連絡先、所属消防士、装備品などの
 * 情報を含みます。
 * 
 * @author FireCaptain Team
 * @version 1.0
 */
@Entity
@Table(name = "fire_stations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class FireStation extends BaseEntity {

    /**
     * 消防署コード（一意）
     * 消防署を識別するための一意のコード
     */
    @Column(name = "station_code", unique = true, nullable = false)
    private String stationCode;

    /**
     * 消防署名
     * 消防署の正式名称
     */
    @Column(name = "station_name", nullable = false)
    private String stationName;

    /**
     * 住所
     * 消防署の所在地住所
     */
    @Column(name = "address", nullable = false)
    private String address;

    /**
     * 緯度
     * 消防署の緯度座標
     */
    @Column(name = "latitude")
    private Double latitude;

    /**
     * 経度
     * 消防署の経度座標
     */
    @Column(name = "longitude")
    private Double longitude;

    /**
     * 電話番号
     * 消防署の連絡用電話番号
     */
    @Column(name = "phone_number")
    private String phoneNumber;

    /**
     * メールアドレス
     * 消防署の連絡用メールアドレス
     */
    @Column(name = "email")
    private String email;

    /**
     * 消防署タイプ
     * 消防署の種類（本署、分署など）
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "station_type")
    private StationType stationType;

    /**
     * 収容能力
     * 消防署が収容できる消防士の最大人数
     */
    @Column(name = "capacity")
    private Integer capacity;

    /**
     * アクティブ状態
     * 消防署が稼働中かどうかのフラグ
     */
    @Column(name = "is_active")
    private Boolean isActive = true;

    /**
     * 所属消防士
     * この消防署に所属する消防士のリスト
     */
    @OneToMany(mappedBy = "fireStation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Firefighter> firefighters;

    /**
     * 装備品
     * この消防署に配備されている装備品のリスト
     */
    @OneToMany(mappedBy = "fireStation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Equipment> equipment;

    /**
     * 消防署の種類を定義する列挙型
     */
    public enum StationType {
        MAIN_STATION, // 本署
        BRANCH_STATION, // 分署
        SUB_STATION, // 出張所
        MOBILE_UNIT // 移動隊
    }
}
