package com.firecaptain.repository;

import com.firecaptain.entity.EmergencyReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.QueryHint;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmergencyReportRepository extends JpaRepository<EmergencyReport, Long> {

        /**
         * 通報番号で検索（キャッシュ有効）
         */
        @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
        Optional<EmergencyReport> findByReportNumber(String reportNumber);

        /**
         * ステータスで検索（キャッシュ有効）
         */
        @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
        List<EmergencyReport> findByStatus(EmergencyReport.ReportStatus status);

        /**
         * 緊急度で検索（キャッシュ有効）
         */
        @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
        List<EmergencyReport> findByPriorityLevel(EmergencyReport.PriorityLevel priorityLevel);

        /**
         * 緊急事態タイプで検索（キャッシュ有効）
         */
        @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
        List<EmergencyReport> findByEmergencyType(EmergencyReport.EmergencyType emergencyType);

        /**
         * 受付日時範囲で検索
         */
        @Query("SELECT er FROM EmergencyReport er WHERE er.receivedAt BETWEEN :startDate AND :endDate")
        List<EmergencyReport> findByReceivedAtBetween(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        /**
         * 受付日時範囲で通報数をカウント
         */
        @Query("SELECT COUNT(er) FROM EmergencyReport er WHERE er.receivedAt BETWEEN :startDate AND :endDate")
        long countByReceivedAtBetween(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        /**
         * ステータスと緊急度で検索
         */
        @Query("SELECT er FROM EmergencyReport er WHERE er.status = :status AND er.priorityLevel = :priorityLevel")
        List<EmergencyReport> findByStatusAndPriorityLevel(@Param("status") EmergencyReport.ReportStatus status,
                        @Param("priorityLevel") EmergencyReport.PriorityLevel priorityLevel);

        /**
         * アクティブな通報を取得（ページネーション対応）
         */
        @Query("SELECT er FROM EmergencyReport er WHERE er.status IN ('RECEIVED', 'DISPATCHED', 'EN_ROUTE', 'ON_SCENE') ORDER BY er.receivedAt DESC")
        Page<EmergencyReport> findActiveReports(Pageable pageable);

        /**
         * 今日の通報数を取得
         */
        @Query("SELECT COUNT(er) FROM EmergencyReport er WHERE DATE(er.receivedAt) = CURRENT_DATE")
        long countTodayReports();

        /**
         * 今月の通報数を取得
         */
        @Query("SELECT COUNT(er) FROM EmergencyReport er WHERE YEAR(er.receivedAt) = YEAR(CURRENT_DATE) AND MONTH(er.receivedAt) = MONTH(CURRENT_DATE)")
        long countThisMonthReports();

        /**
         * 緊急度別の通報数を取得
         */
        @Query("SELECT er.priorityLevel, COUNT(er) FROM EmergencyReport er GROUP BY er.priorityLevel")
        List<Object[]> countByPriorityLevel();

        /**
         * 緊急事態タイプ別の通報数を取得
         */
        @Query("SELECT er.emergencyType, COUNT(er) FROM EmergencyReport er GROUP BY er.emergencyType")
        List<Object[]> countByEmergencyType();

        /**
         * 平均応答時間を取得
         */
        @Query("SELECT AVG(EXTRACT(EPOCH FROM (er.dispatchedAt - er.receivedAt))/60) FROM EmergencyReport er WHERE er.dispatchedAt IS NOT NULL")
        Double getAverageResponseTimeMinutes();

        /**
         * 指定したステータスの通報数を取得
         */
        @Query("SELECT COUNT(er) FROM EmergencyReport er WHERE er.status IN :statuses")
        long countByStatusIn(@Param("statuses") List<EmergencyReport.ReportStatus> statuses);

        /**
         * 最新の通報を取得（制限付き）
         */
        @Query("SELECT er FROM EmergencyReport er ORDER BY er.receivedAt DESC")
        List<EmergencyReport> findLatestReports(Pageable pageable);

        /**
         * 地理的範囲で検索（PostGIS使用）
         */
        @Query(value = "SELECT * FROM emergency_reports WHERE ST_DWithin(location, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326), :radiusMeters)", nativeQuery = true)
        List<EmergencyReport> findByLocationWithinRadius(@Param("longitude") Double longitude,
                        @Param("latitude") Double latitude,
                        @Param("radiusMeters") Double radiusMeters);

        /**
         * パフォーマンス最適化：インデックスヒント
         */
        @QueryHints({
                        @QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"),
                        @QueryHint(name = org.hibernate.annotations.QueryHints.CACHE_REGION, value = "emergencyReports")
        })
        @Query("SELECT er FROM EmergencyReport er WHERE er.status = :status ORDER BY er.receivedAt DESC")
        List<EmergencyReport> findByStatusOrderByReceivedAtDesc(@Param("status") EmergencyReport.ReportStatus status);
}
