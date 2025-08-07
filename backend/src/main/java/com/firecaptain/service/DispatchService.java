package com.firecaptain.service;

import com.firecaptain.entity.Dispatch;
import com.firecaptain.entity.DispatchUnit;
import com.firecaptain.entity.EmergencyReport;
import com.firecaptain.entity.Unit;
import com.firecaptain.repository.DispatchRepository;
import com.firecaptain.repository.UnitRepository;
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

/**
 * 出動指令管理サービス
 * 
 * 緊急通報に基づく出動指令の作成、管理、部隊割り当てなどの
 * ビジネスロジックを提供します。最適な部隊の選択、到着時間の
 * 計算、現場支援の判断などの高度な機能を含みます。
 * 
 * @author FireCaptain Team
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DispatchService {

    private final DispatchRepository dispatchRepository;
    private final UnitRepository unitRepository;
    private final SceneSupportService sceneSupportService;

    /**
     * 緊急通報から出動指令を作成
     * 
     * 緊急通報の内容に基づいて出動指令を自動生成し、
     * 適切な部隊の割り当てを行います。
     * 
     * @param report 緊急通報情報
     * @return 作成された出動指令
     */
    @CacheEvict(value = "dispatches", allEntries = true)
    public Dispatch createDispatchFromReport(EmergencyReport report) {
        log.info("Creating dispatch from emergency report: {}", report.getReportNumber());

        Dispatch dispatch = new Dispatch();
        dispatch.setDispatchNumber(generateDispatchNumber());
        dispatch.setEmergencyReport(report);
        dispatch.setDispatchType(determineDispatchType(report.getEmergencyType()));
        dispatch.setPriorityLevel(report.getPriorityLevel());
        dispatch.setStatus(Dispatch.DispatchStatus.DISPATCHED);
        dispatch.setDispatchedAt(LocalDateTime.now());

        Dispatch savedDispatch = dispatchRepository.save(dispatch);

        // 非同期で部隊割り当てを実行
        assignUnitsAsync(savedDispatch);

        return savedDispatch;
    }

    /**
     * 指令番号による出動指令の検索（キャッシュ付き）
     * 
     * @param dispatchNumber 指令番号
     * @return 出動指令情報
     */
    @Cacheable(value = "dispatches", key = "#dispatchNumber")
    public Optional<Dispatch> findByDispatchNumber(String dispatchNumber) {
        log.debug("Finding dispatch by number: {}", dispatchNumber);
        Dispatch dispatch = dispatchRepository.findByDispatchNumber(dispatchNumber);
        return Optional.ofNullable(dispatch);
    }

    /**
     * ステータスによる出動指令の検索
     * 
     * @param status 指令ステータス
     * @return 指定ステータスの出動指令リスト
     */
    public List<Dispatch> findByStatus(Dispatch.DispatchStatus status) {
        log.debug("Finding dispatches by status: {}", status);
        return dispatchRepository.findByStatus(status);
    }

    /**
     * 出動指令の更新
     * 
     * @param dispatch 更新する出動指令情報
     * @return 更新された出動指令
     */
    @CacheEvict(value = "dispatches", key = "#dispatch.dispatchNumber")
    public Dispatch updateDispatch(Dispatch dispatch) {
        log.info("Updating dispatch: {}", dispatch.getDispatchNumber());
        return dispatchRepository.save(dispatch);
    }

    /**
     * 出動指令ステータスの更新
     * 
     * ステータス変更に応じて適切なタイムスタンプを自動更新します。
     * 
     * @param dispatchNumber 指令番号
     * @param status         新しいステータス
     * @return 更新された出動指令
     */
    @CacheEvict(value = "dispatches", key = "#dispatchNumber")
    public Dispatch updateStatus(String dispatchNumber, Dispatch.DispatchStatus status) {
        log.info("Updating dispatch status: {} -> {}", dispatchNumber, status);

        Dispatch dispatch = dispatchRepository.findByDispatchNumber(dispatchNumber);
        if (dispatch == null) {
            throw new RuntimeException("Dispatch not found: " + dispatchNumber);
        }

        dispatch.setStatus(status);

        // ステータスに応じてタイムスタンプを更新
        switch (status) {
            case ON_SCENE -> dispatch.setActualArrivalTime(LocalDateTime.now()); // 現場到着時刻
            case COMPLETED -> dispatch.setCompletedAt(LocalDateTime.now()); // 完了時刻
        }

        return dispatchRepository.save(dispatch);
    }

    /**
     * 部隊を指令に割り当て
     */
    @CacheEvict(value = "dispatches", key = "#dispatch.dispatchNumber")
    public DispatchUnit assignUnitToDispatch(Dispatch dispatch, Unit unit) {
        log.info("Assigning unit {} to dispatch: {}", unit.getUnitNumber(), dispatch.getDispatchNumber());

        DispatchUnit dispatchUnit = new DispatchUnit();
        dispatchUnit.setDispatch(dispatch);
        dispatchUnit.setUnit(unit);
        dispatchUnit.setStatus(DispatchUnit.UnitStatus.DISPATCHED);
        dispatchUnit.setDispatchedAt(LocalDateTime.now());

        // 到着予想時間を計算
        LocalDateTime estimatedArrival = calculateEstimatedArrival(dispatch, unit);
        dispatchUnit.setEstimatedArrivalTime(estimatedArrival);

        return dispatchUnit;
    }

    /**
     * 非同期で部隊割り当てを実行
     */
    @Async
    public void assignUnitsAsync(Dispatch dispatch) {
        try {
            log.info("Assigning units to dispatch: {}", dispatch.getDispatchNumber());

            // 利用可能な部隊を検索
            // DispatchTypeをUnitTypeに変換する必要がある
            Unit.UnitType unitType = convertDispatchTypeToUnitType(dispatch.getDispatchType());
            List<Unit> availableUnits = unitRepository.findAvailableUnitsByType(unitType);

            // 優先度に基づいて部隊を選択
            List<Unit> selectedUnits = selectOptimalUnits(availableUnits, dispatch);

            // 部隊を割り当て
            for (Unit unit : selectedUnits) {
                assignUnitToDispatch(dispatch, unit);
            }

            // 必要に応じて現場支援を要求
            if (requiresSceneSupport(dispatch)) {
                sceneSupportService.requestSceneSupport(dispatch);
            }

        } catch (Exception e) {
            log.error("Error assigning units to dispatch: {}", dispatch.getDispatchNumber(), e);
        }
    }

    /**
     * 指令番号を生成
     */
    private String generateDispatchNumber() {
        String timestamp = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "DISP-" + timestamp + "-" + random;
    }

    /**
     * 通報タイプから指令タイプを決定
     */
    private Dispatch.DispatchType determineDispatchType(EmergencyReport.EmergencyType emergencyType) {
        return switch (emergencyType) {
            case FIRE -> Dispatch.DispatchType.FIRE_ENGINE;
            case MEDICAL -> Dispatch.DispatchType.AMBULANCE;
            case TRAFFIC_ACCIDENT -> Dispatch.DispatchType.RESCUE_UNIT;
            case HAZMAT -> Dispatch.DispatchType.HAZMAT_UNIT;
            case RESCUE -> Dispatch.DispatchType.RESCUE_UNIT;
            case OTHER -> Dispatch.DispatchType.COMMAND_UNIT;
        };
    }

    /**
     * 最適な部隊を選択
     */
    private List<Unit> selectOptimalUnits(List<Unit> availableUnits, Dispatch dispatch) {
        // 距離、可用性、能力に基づいて最適な部隊を選択
        // 実装は簡略化
        return availableUnits.stream()
                .limit(3) // 最大3部隊まで
                .toList();
    }

    /**
     * 到着予想時間を計算
     */
    private LocalDateTime calculateEstimatedArrival(Dispatch dispatch, Unit unit) {
        // 距離と平均速度から到着時間を計算
        // 実装は簡略化
        return LocalDateTime.now().plusMinutes(15);
    }

    /**
     * 現場支援が必要かどうかを判定
     */
    private boolean requiresSceneSupport(Dispatch dispatch) {
        // 緊急度や状況に基づいて判定
        return dispatch.getPriorityLevel() == EmergencyReport.PriorityLevel.CRITICAL ||
                dispatch.getPriorityLevel() == EmergencyReport.PriorityLevel.HIGH;
    }

    /**
     * DispatchTypeをUnitTypeに変換
     */
    private Unit.UnitType convertDispatchTypeToUnitType(Dispatch.DispatchType dispatchType) {
        return switch (dispatchType) {
            case FIRE_ENGINE -> Unit.UnitType.FIRE_ENGINE;
            case AMBULANCE -> Unit.UnitType.AMBULANCE;
            case LADDER_TRUCK -> Unit.UnitType.LADDER_TRUCK;
            case RESCUE_UNIT -> Unit.UnitType.RESCUE_VEHICLE;
            case HAZMAT_UNIT -> Unit.UnitType.SPECIAL_UNIT;
            case COMMAND_UNIT -> Unit.UnitType.COMMAND_VEHICLE;
        };
    }

    /**
     * 統計情報を取得
     */
    public DispatchStatistics getStatistics() {
        log.debug("Getting dispatch statistics");

        long totalDispatches = dispatchRepository.count();
        long activeDispatches = dispatchRepository.countByStatusIn(
                List.of(Dispatch.DispatchStatus.DISPATCHED, Dispatch.DispatchStatus.EN_ROUTE,
                        Dispatch.DispatchStatus.ON_SCENE));

        return DispatchStatistics.builder()
                .totalDispatches(totalDispatches)
                .activeDispatches(activeDispatches)
                .build();
    }

    /**
     * 統計情報クラス
     */
    public static class DispatchStatistics {
        private final long totalDispatches;
        private final long activeDispatches;

        public DispatchStatistics(long totalDispatches, long activeDispatches) {
            this.totalDispatches = totalDispatches;
            this.activeDispatches = activeDispatches;
        }

        public long getTotalDispatches() {
            return totalDispatches;
        }

        public long getActiveDispatches() {
            return activeDispatches;
        }

        public static DispatchStatisticsBuilder builder() {
            return new DispatchStatisticsBuilder();
        }

        public static class DispatchStatisticsBuilder {
            private long totalDispatches;
            private long activeDispatches;

            public DispatchStatisticsBuilder totalDispatches(long totalDispatches) {
                this.totalDispatches = totalDispatches;
                return this;
            }

            public DispatchStatisticsBuilder activeDispatches(long activeDispatches) {
                this.activeDispatches = activeDispatches;
                return this;
            }

            public DispatchStatistics build() {
                return new DispatchStatistics(totalDispatches, activeDispatches);
            }
        }
    }
}
