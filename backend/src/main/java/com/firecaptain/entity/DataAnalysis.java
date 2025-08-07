package com.firecaptain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * データ分析エンティティ
 * 
 * 消防活動のデータ分析結果を管理するエンティティです。
 * 
 * @author FireCaptain Team
 * @version 1.0
 */
@Entity
@Table(name = "data_analyses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 分析タイプ */
    @Enumerated(EnumType.STRING)
    @Column(name = "analysis_type", nullable = false)
    private AnalysisType analysisType;

    /** 分析タイトル */
    @Column(name = "title", nullable = false)
    private String title;

    /** 分析内容 */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /** 分析対象期間（開始） */
    @Column(name = "analysis_start_date")
    private LocalDateTime analysisStartDate;

    /** 分析対象期間（終了） */
    @Column(name = "analysis_end_date")
    private LocalDateTime analysisEndDate;

    /** 分析実行日時 */
    @Column(name = "analysis_date", nullable = false)
    private LocalDateTime analysisDate;

    /** 開始日時（旧フィールド名との互換性） */
    @Transient
    private LocalDateTime startDate;

    /** 終了日時（旧フィールド名との互換性） */
    @Transient
    private LocalDateTime endDate;

    /** 分析結果（JSON形式） */
    @Column(name = "analysis_result", columnDefinition = "TEXT")
    private String analysisResult;

    /** 分析ID */
    @Column(name = "analysis_id", unique = true)
    private String analysisId;

    /** 時間期間 */
    @Enumerated(EnumType.STRING)
    @Column(name = "time_period")
    private TimePeriod timePeriod;

    /** 分析ステータス */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AnalysisStatus status = AnalysisStatus.PENDING;

    /** 事案件数 */
    @Column(name = "total_incidents")
    private Integer totalIncidents;

    /** 平均応答時間（分） */
    @Column(name = "average_response_time_minutes")
    private Double averageResponseTimeMinutes;

    /** 平均移動時間（分） */
    @Column(name = "average_travel_time_minutes")
    private Double averageTravelTimeMinutes;

    /** 総運用コスト */
    @Column(name = "total_operational_cost")
    private Double totalOperationalCost;

    /** 傾向分析 */
    @Column(name = "trends", columnDefinition = "TEXT")
    private String trends;

    /** ホットスポット */
    @Column(name = "hotspots", columnDefinition = "TEXT")
    private String hotspots;

    /** パターン分析 */
    @Column(name = "patterns", columnDefinition = "TEXT")
    private String patterns;

    /** ピーク時間 */
    @Column(name = "peak_hours", columnDefinition = "TEXT")
    private String peakHours;

    /** 洞察 */
    @Column(name = "insights", columnDefinition = "TEXT")
    private String insights;

    /** 生成日時 */
    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    /** 生成者 */
    @Column(name = "generated_by")
    private String generatedBy;

    /** 最終更新日時 */
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    /** 作成者 */
    @Column(name = "created_by")
    private String createdBy;

    /** 作成日時 */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /** 更新日時 */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 分析タイプの列挙型
     */
    public enum AnalysisType {
        RESPONSE_TIME_ANALYSIS, // 応答時間分析
        INCIDENT_FREQUENCY, // 事案発生頻度
        RESOURCE_UTILIZATION, // リソース利用率
        PERFORMANCE_METRICS, // パフォーマンス指標
        TREND_ANALYSIS, // 傾向分析
        COMPARATIVE_ANALYSIS, // 比較分析
        INCIDENT_VOLUME, // 事案件数
        RESPONSE_TIME, // 応答時間
        COST_ANALYSIS, // コスト分析
        GEOGRAPHIC_DISTRIBUTION, // 地理的分布
        PREDICTIVE_MODELING, // 予測モデリング
        OTHER // その他
    }

    /**
     * 時間期間の列挙型
     */
    public enum TimePeriod {
        DAILY, // 日次
        WEEKLY, // 週次
        MONTHLY, // 月次
        QUARTERLY, // 四半期
        YEARLY, // 年次
        CUSTOM // カスタム
    }

    /**
     * 分析ステータスの列挙型
     */
    public enum AnalysisStatus {
        PENDING, // 待機中
        IN_PROGRESS, // 進行中
        COMPLETED, // 完了
        FAILED, // 失敗
        ARCHIVED // アーカイブ
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
    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public void setAverageTravelTimeMinutes(double averageTravelTimeMinutes) {
        this.averageTravelTimeMinutes = averageTravelTimeMinutes;
    }

    public void setTotalOperationalCost(double totalOperationalCost) {
        this.totalOperationalCost = totalOperationalCost;
    }

    public void setTrends(String trends) {
        this.trends = trends;
    }

    public void setHotspots(String hotspots) {
        this.hotspots = hotspots;
    }

    public void setPatterns(String patterns) {
        this.patterns = patterns;
    }
}
