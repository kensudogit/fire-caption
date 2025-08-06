package com.firecaptain.service;

import com.firecaptain.entity.Dispatch;
import com.firecaptain.entity.SceneSupport;
import com.firecaptain.entity.EmergencyReport;
import com.firecaptain.repository.SceneSupportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SceneSupportService {

    private final SceneSupportRepository sceneSupportRepository;

    /**
     * 現場支援を要求
     */
    @CacheEvict(value = "sceneSupports", allEntries = true)
    public SceneSupport requestSceneSupport(Dispatch dispatch) {
        log.info("Requesting scene support for dispatch: {}", dispatch.getDispatchNumber());

        SceneSupport support = new SceneSupport();
        support.setDispatch(dispatch);
        support.setSupportType(determineSupportType(dispatch));
        support.setStatus(SceneSupport.SupportStatus.REQUESTED);
        support.setRequestedAt(LocalDateTime.now());
        support.setPriorityLevel(dispatch.getPriorityLevel());
        support.setDescription("Automatic support request for " + dispatch.getDispatchType());

        return sceneSupportRepository.save(support);
    }

    /**
     * 現場支援を承認
     */
    @CacheEvict(value = "sceneSupports", key = "#supportId")
    public SceneSupport approveSceneSupport(Long supportId) {
        log.info("Approving scene support: {}", supportId);

        SceneSupport support = sceneSupportRepository.findById(supportId)
                .orElseThrow(() -> new RuntimeException("Scene support not found: " + supportId));

        support.setStatus(SceneSupport.SupportStatus.APPROVED);
        support.setApprovedAt(LocalDateTime.now());

        return sceneSupportRepository.save(support);
    }

    /**
     * 現場支援を出動
     */
    @CacheEvict(value = "sceneSupports", key = "#supportId")
    public SceneSupport dispatchSceneSupport(Long supportId) {
        log.info("Dispatching scene support: {}", supportId);

        SceneSupport support = sceneSupportRepository.findById(supportId)
                .orElseThrow(() -> new RuntimeException("Scene support not found: " + supportId));

        support.setStatus(SceneSupport.SupportStatus.DISPATCHED);
        support.setDispatchedAt(LocalDateTime.now());

        return sceneSupportRepository.save(support);
    }

    /**
     * 現場支援を完了
     */
    @CacheEvict(value = "sceneSupports", key = "#supportId")
    public SceneSupport completeSceneSupport(Long supportId, String summary, Double actualCost) {
        log.info("Completing scene support: {}", supportId);

        SceneSupport support = sceneSupportRepository.findById(supportId)
                .orElseThrow(() -> new RuntimeException("Scene support not found: " + supportId));

        support.setStatus(SceneSupport.SupportStatus.COMPLETED);
        support.setCompletedAt(LocalDateTime.now());
        support.setSummary(summary);
        support.setActualCost(actualCost);

        // 実際の所要時間を計算
        if (support.getRequestedAt() != null && support.getCompletedAt() != null) {
            long durationMinutes = java.time.Duration.between(support.getRequestedAt(), support.getCompletedAt())
                    .toMinutes();
            support.setActualDurationMinutes((int) durationMinutes);
        }

        return sceneSupportRepository.save(support);
    }

    /**
     * 支援IDで検索
     */
    @Cacheable(value = "sceneSupports", key = "#supportId")
    public Optional<SceneSupport> findById(Long supportId) {
        log.debug("Finding scene support by ID: {}", supportId);
        return sceneSupportRepository.findById(supportId);
    }

    /**
     * ステータスで検索
     */
    public List<SceneSupport> findByStatus(SceneSupport.SupportStatus status) {
        log.debug("Finding scene supports by status: {}", status);
        return sceneSupportRepository.findByStatus(status);
    }

    /**
     * 指令で検索
     */
    public List<SceneSupport> findByDispatch(Dispatch dispatch) {
        log.debug("Finding scene supports for dispatch: {}", dispatch.getDispatchNumber());
        return sceneSupportRepository.findByDispatch(dispatch);
    }

    /**
     * 支援タイプを決定
     */
    private SceneSupport.SupportType determineSupportType(Dispatch dispatch) {
        return switch (dispatch.getDispatchType()) {
            case FIRE_ENGINE -> SceneSupport.SupportType.ADDITIONAL_UNITS;
            case AMBULANCE -> SceneSupport.SupportType.MEDICAL_SUPPORT;
            case RESCUE_UNIT -> SceneSupport.SupportType.SPECIALIZED_EQUIPMENT;
            case HAZMAT_UNIT -> SceneSupport.SupportType.TECHNICAL_SUPPORT;
            default -> SceneSupport.SupportType.LOGISTICS_SUPPORT;
        };
    }

    /**
     * 統計情報を取得
     */
    public SceneSupportStatistics getStatistics() {
        log.debug("Getting scene support statistics");

        long totalSupports = sceneSupportRepository.count();
        long activeSupports = sceneSupportRepository.countByStatusIn(
                List.of(SceneSupport.SupportStatus.REQUESTED, SceneSupport.SupportStatus.APPROVED,
                        SceneSupport.SupportStatus.DISPATCHED, SceneSupport.SupportStatus.ON_SCENE));

        return SceneSupportStatistics.builder()
                .totalSupports(totalSupports)
                .activeSupports(activeSupports)
                .build();
    }

    /**
     * 統計情報クラス
     */
    public static class SceneSupportStatistics {
        private final long totalSupports;
        private final long activeSupports;

        public SceneSupportStatistics(long totalSupports, long activeSupports) {
            this.totalSupports = totalSupports;
            this.activeSupports = activeSupports;
        }

        public long getTotalSupports() {
            return totalSupports;
        }

        public long getActiveSupports() {
            return activeSupports;
        }

        public static SceneSupportStatisticsBuilder builder() {
            return new SceneSupportStatisticsBuilder();
        }

        public static class SceneSupportStatisticsBuilder {
            private long totalSupports;
            private long activeSupports;

            public SceneSupportStatisticsBuilder totalSupports(long totalSupports) {
                this.totalSupports = totalSupports;
                return this;
            }

            public SceneSupportStatisticsBuilder activeSupports(long activeSupports) {
                this.activeSupports = activeSupports;
                return this;
            }

            public SceneSupportStatistics build() {
                return new SceneSupportStatistics(totalSupports, activeSupports);
            }
        }
    }
}
