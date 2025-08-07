package com.firecaptain.repository;

import com.firecaptain.entity.SceneSupport;
import com.firecaptain.entity.Dispatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 現場支援リポジトリ
 * 
 * 現場支援情報のデータアクセスを担当するリポジトリです。
 * 
 * @author FireCaptain Team
 * @version 1.0
 */
@Repository
public interface SceneSupportRepository extends JpaRepository<SceneSupport, Long> {

    /**
     * 出動IDで現場支援を検索
     */
    List<SceneSupport> findByDispatchId(Long dispatchId);

    /**
     * ステータスで現場支援を検索
     */
    List<SceneSupport> findByStatus(SceneSupport.SupportStatus status);

    /**
     * 出動で現場支援を検索
     */
    List<SceneSupport> findByDispatch(Dispatch dispatch);

    /**
     * 要求日時範囲で現場支援を検索
     */
    List<SceneSupport> findByRequestedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 複数のステータスで現場支援数をカウント
     */
    long countByStatusIn(List<SceneSupport.SupportStatus> statuses);
}
