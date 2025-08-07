package com.firecaptain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * 出動更新情報エンティティ
 * 
 * 出動指令の状態変更や進捗更新を記録するエンティティです。
 * 各更新はタイムスタンプと共に記録され、出動の履歴を追跡できます。
 * 
 * @author FireCaptain Team
 * @version 1.0
 */
@Entity
@Table(name = "dispatch_updates")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DispatchUpdate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 関連する出動指令 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispatch_id", nullable = false)
    private Dispatch dispatch;

    /** 更新タイプ */
    @Enumerated(EnumType.STRING)
    @Column(name = "update_type", nullable = false)
    private UpdateType updateType;

    /** 更新前のステータス */
    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status")
    private Dispatch.DispatchStatus previousStatus;

    /** 更新後のステータス */
    @Enumerated(EnumType.STRING)
    @Column(name = "new_status")
    private Dispatch.DispatchStatus newStatus;

    /** 更新内容の詳細 */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /** 更新者（オペレーターID） */
    @Column(name = "updated_by")
    private String updatedBy;

    /** 更新日時 */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /** 位置情報（緯度） */
    @Column(name = "latitude")
    private Double latitude;

    /** 位置情報（経度） */
    @Column(name = "longitude")
    private Double longitude;

    /** 備考 */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * 更新タイプの列挙型
     */
    public enum UpdateType {
        STATUS_CHANGE, // ステータス変更
        LOCATION_UPDATE, // 位置情報更新
        UNIT_ASSIGNMENT, // 部隊割り当て
        ETA_UPDATE, // 到着予定時刻更新
        SCENE_ARRIVAL, // 現場到着
        COMPLETION, // 完了
        CANCELLATION, // キャンセル
        OTHER // その他
    }

    @PrePersist
    protected void onCreate() {
        updatedAt = LocalDateTime.now();
    }
}
