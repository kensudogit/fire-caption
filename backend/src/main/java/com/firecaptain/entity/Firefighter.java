package com.firecaptain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

/**
 * 消防士エンティティ
 * 
 * 消防署に所属する消防士の情報を管理するエンティティクラスです。
 * 個人情報、階級、所属消防署、資格情報などを含みます。
 * 
 * @author FireCaptain Team
 * @version 1.0
 */
@Entity
@Table(name = "firefighters")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Firefighter extends BaseEntity {

    /**
     * 従業員ID（一意）
     * 消防士を識別するための一意のID
     */
    @Column(name = "employee_id", unique = true, nullable = false)
    private String employeeId;

    /**
     * 名
     * 消防士の名前（名）
     */
    @Column(name = "first_name", nullable = false)
    private String firstName;

    /**
     * 姓
     * 消防士の名前（姓）
     */
    @Column(name = "last_name", nullable = false)
    private String lastName;

    /**
     * メールアドレス（一意）
     * 消防士の連絡用メールアドレス
     */
    @Column(name = "email", unique = true)
    private String email;

    /**
     * 電話番号
     * 消防士の連絡用電話番号
     */
    @Column(name = "phone_number")
    private String phoneNumber;

    /**
     * 生年月日
     * 消防士の生年月日
     */
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    /**
     * 入職日
     * 消防士として入職した日付
     */
    @Column(name = "hire_date")
    private LocalDate hireDate;

    /**
     * 階級
     * 消防士の階級（消防士、隊長など）
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "rank")
    private Rank rank;

    /**
     * ステータス
     * 消防士の現在の状況（在職中、休暇中など）
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status = Status.ACTIVE;

    /**
     * 所属消防署
     * この消防士が所属する消防署
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fire_station_id")
    private FireStation fireStation;

    /**
     * 担当緊急通報
     * この消防士が担当した緊急通報のリスト
     */
    @OneToMany(mappedBy = "firefighter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EmergencyCall> emergencyCalls;

    /**
     * 資格情報
     * この消防士が保有する資格のリスト
     */
    @OneToMany(mappedBy = "firefighter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Certification> certifications;

    /**
     * 消防士の階級を定義する列挙型
     */
    public enum Rank {
        FIREFIGHTER, // 消防士
        SENIOR_FIREFIGHTER, // 上級消防士
        LIEUTENANT, // 副隊長
        CAPTAIN, // 隊長
        BATTALION_CHIEF, // 大隊長
        DEPUTY_CHIEF, // 副署長
        FIRE_CHIEF // 署長
    }

    /**
     * 消防士のステータスを定義する列挙型
     */
    public enum Status {
        ACTIVE, // 在職中
        ON_LEAVE, // 休暇中
        RETIRED, // 退職
        SUSPENDED // 停職
    }
}
