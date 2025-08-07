package com.firecaptain.repository;

import com.firecaptain.entity.Dispatch;
import com.firecaptain.entity.EmergencyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 出動リポジトリ
 * 
 * 出動情報のデータアクセスを担当するリポジトリです。
 * 出動の検索、状態管理、統計情報の取得などの機能を提供します。
 * 
 * @author FireCaptain Team
 * @version 1.0
 */
@Repository
public interface DispatchRepository extends JpaRepository<Dispatch, Long> {

    /**
     * 出動番号で出動を検索
     * 
     * @param dispatchNumber 出動番号
     * @return 出動（見つからない場合はnull）
     */
    Dispatch findByDispatchNumber(String dispatchNumber);

    /**
     * ステータスで出動を検索
     * 
     * @param status 出動ステータス
     * @return 該当する出動のリスト
     */
    List<Dispatch> findByStatus(Dispatch.DispatchStatus status);

    /**
     * 緊急通報IDで出動を検索
     * 
     * @param emergencyReportId 緊急通報ID
     * @return 該当する出動のリスト
     */
    List<Dispatch> findByEmergencyReportId(Long emergencyReportId);

    /**
     * 出動タイプで出動を検索
     * 
     * @param dispatchType 出動タイプ
     * @return 該当する出動のリスト
     */
    List<Dispatch> findByDispatchType(Dispatch.DispatchType dispatchType);

    /**
     * 優先度で出動を検索
     * 
     * @param priorityLevel 優先度
     * @return 該当する出動のリスト
     */
    List<Dispatch> findByPriorityLevel(EmergencyReport.PriorityLevel priorityLevel);

    /**
     * 指定された期間の出動を検索
     * 
     * @param startDate 開始日時
     * @param endDate   終了日時
     * @return 該当する出動のリスト
     */
    @Query("SELECT d FROM Dispatch d WHERE d.dispatchedAt BETWEEN :startDate AND :endDate")
    List<Dispatch> findByDispatchedAtBetween(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * アクティブな出動を検索
     * 
     * @return アクティブな出動のリスト
     */
    @Query("SELECT d FROM Dispatch d WHERE d.status IN ('DISPATCHED', 'EN_ROUTE', 'ON_SCENE')")
    List<Dispatch> findActiveDispatches();

    /**
     * 指定された消防署の出動を検索
     * 
     * @param fireStationId 消防署ID
     * @return 該当する出動のリスト
     */
    @Query("SELECT d FROM Dispatch d JOIN d.dispatchUnits du JOIN du.unit u WHERE u.fireStation.id = :fireStationId")
    List<Dispatch> findByFireStationId(@Param("fireStationId") Long fireStationId);

    /**
     * 出動番号の存在確認
     * 
     * @param dispatchNumber 出動番号
     * @return 存在する場合はtrue
     */
    boolean existsByDispatchNumber(String dispatchNumber);

    /**
     * 指定されたステータスの出動数をカウント
     * 
     * @param statuses ステータスリスト
     * @return 該当する出動数
     */
    long countByStatusIn(List<Dispatch.DispatchStatus> statuses);

    /**
     * 指定された期間の出動統計を取得
     * 
     * @param startDate 開始日時
     * @param endDate   終了日時
     * @return 出動統計情報
     */
    @Query("SELECT COUNT(d) as total, " +
            "SUM(CASE WHEN d.status = 'COMPLETED' THEN 1 ELSE 0 END) as completed, " +
            "AVG(CASE WHEN d.actualArrivalTime IS NOT NULL AND d.dispatchedAt IS NOT NULL " +
            "THEN EXTRACT(EPOCH FROM (d.actualArrivalTime - d.dispatchedAt)) * 1000 ELSE NULL END) as avgResponseTime "
            +
            "FROM Dispatch d WHERE d.dispatchedAt BETWEEN :startDate AND :endDate")
    Object[] getDispatchStatistics(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
