package com.firecaptain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * 現場支援エンティティ
 * 
 * 出動指令に対する現場支援の要求と管理を行うエンティティです。
 * 
 * @author FireCaptain Team
 * @version 1.0
 */
@Entity
@Table(name = "scene_supports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SceneSupport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 関連する出動指令 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispatch_id", nullable = false)
    private Dispatch dispatch;

    /** 支援タイプ */
    @Enumerated(EnumType.STRING)
    @Column(name = "support_type", nullable = false)
    private SupportType supportType;

    /** 支援ステータス */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SupportStatus status = SupportStatus.REQUESTED;

    /** 優先度 */
    @Enumerated(EnumType.STRING)
    @Column(name = "priority_level", nullable = false)
    private EmergencyReport.PriorityLevel priorityLevel;

    /** 支援内容の詳細 */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /** 要求日時 */
    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    /** 承認日時 */
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    /** 開始日時 */
    @Column(name = "started_at")
    private LocalDateTime startedAt;

    /** 完了日時 */
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    /** 出動日時 */
    @Column(name = "dispatched_at")
    private LocalDateTime dispatchedAt;

    /** 実際のコスト */
    @Column(name = "actual_cost")
    private Double actualCost;

    /** 実際の所要時間（分） */
    @Column(name = "actual_duration_minutes")
    private Integer actualDurationMinutes;

    /** サマリー */
    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    /** 作成日時 */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /** 更新日時 */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 支援タイプの列挙型
     */
    public enum SupportType {
        ADDITIONAL_UNITS, // 追加部隊
        SPECIALIZED_EQUIPMENT, // 特殊装備
        MEDICAL_SUPPORT, // 医療支援
        TECHNICAL_SUPPORT, // 技術支援
        LOGISTICS_SUPPORT, // 後方支援
        OTHER // その他
    }

    /**
     * 支援ステータスの列挙型
     */
    public enum SupportStatus {
        REQUESTED, // 要求中
        APPROVED, // 承認済み
        DISPATCHED, // 出動中
        ON_SCENE, // 現場到着
        IN_PROGRESS, // 進行中
        COMPLETED, // 完了
        CANCELLED // キャンセル
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
    public Dispatch getDispatch() {
        return dispatch;
    }

    public void setDispatch(Dispatch dispatch) {
        this.dispatch = dispatch;
    }

    public SupportType getSupportType() {
        return supportType;
    }

    public void setSupportType(SupportType supportType) {
        this.supportType = supportType;
    }

    public SupportStatus getStatus() {
        return status;
    }

    public void setStatus(SupportStatus status) {
        this.status = status;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public LocalDateTime getDispatchedAt() {
        return dispatchedAt;
    }

    public void setDispatchedAt(LocalDateTime dispatchedAt) {
        this.dispatchedAt = dispatchedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Double getActualCost() {
        return actualCost;
    }

    public void setActualCost(Double actualCost) {
        this.actualCost = actualCost;
    }

    public Integer getActualDurationMinutes() {
        return actualDurationMinutes;
    }

    public void setActualDurationMinutes(Integer actualDurationMinutes) {
        this.actualDurationMinutes = actualDurationMinutes;
    }
}
