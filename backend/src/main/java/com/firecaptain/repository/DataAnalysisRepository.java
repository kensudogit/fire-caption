package com.firecaptain.repository;

import com.firecaptain.entity.DataAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * データ分析リポジトリ
 * 
 * データ分析情報のデータアクセスを担当するリポジトリです。
 * 
 * @author FireCaptain Team
 * @version 1.0
 */
@Repository
public interface DataAnalysisRepository extends JpaRepository<DataAnalysis, Long> {

    /**
     * 指定された期間のデータ分析を検索
     */
    List<DataAnalysis> findByAnalysisDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 分析タイプでデータ分析を検索
     */
    List<DataAnalysis> findByAnalysisType(DataAnalysis.AnalysisType analysisType);

    /**
     * 分析IDでデータ分析を検索
     */
    Optional<DataAnalysis> findByAnalysisId(String analysisId);

    /**
     * 生成日時順で最新の分析を取得
     */
    List<DataAnalysis> findTopByOrderByGeneratedAtDesc(int limit);

    /**
     * ステータスで分析数をカウント
     */
    long countByStatus(DataAnalysis.AnalysisStatus status);
}
