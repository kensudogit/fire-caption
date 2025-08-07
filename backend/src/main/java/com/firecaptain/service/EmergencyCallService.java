package com.firecaptain.service;

import com.firecaptain.entity.EmergencyCall;
import com.firecaptain.entity.FireStation;
import com.firecaptain.entity.Firefighter;
import com.firecaptain.repository.EmergencyCallRepository;
import com.firecaptain.repository.FireStationRepository;
import com.firecaptain.repository.FirefighterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * 緊急通報管理サービス
 * 
 * 緊急通報の作成、更新、検索などのビジネスロジックを提供します。
 * キャッシュ機能、非同期処理、最適な消防署の自動割り当てなどの
 * 高度な機能を含みます。
 * 
 * @author FireCaptain Team
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EmergencyCallService {

    private final EmergencyCallRepository emergencyCallRepository;
    private final FireStationRepository fireStationRepository;
    private final FirefighterRepository firefighterRepository;
    private final NotificationService notificationService;

    /**
     * IDによる緊急通報の取得（キャッシュ付き）
     * 
     * @param id 緊急通報のID
     * @return 緊急通報情報
     */
    @Cacheable(value = "emergencyCalls", key = "#id")
    public Optional<EmergencyCall> findById(Long id) {
        return emergencyCallRepository.findById(id);
    }

    /**
     * 通報番号による緊急通報の取得（キャッシュ付き）
     * 
     * @param callNumber 通報番号
     * @return 緊急通報情報
     */
    @Cacheable(value = "emergencyCalls", key = "#callNumber")
    public Optional<EmergencyCall> findByCallNumber(String callNumber) {
        return emergencyCallRepository.findByCallNumber(callNumber);
    }

    /**
     * ステータスによる緊急通報の検索
     * 
     * @param status   通報ステータス
     * @param pageable ページネーション情報
     * @return 指定ステータスの緊急通報のページ
     */
    public Page<EmergencyCall> findByStatus(EmergencyCall.CallStatus status, Pageable pageable) {
        return emergencyCallRepository.findByStatus(status, pageable);
    }

    /**
     * アクティブな緊急通報の取得（キャッシュ付き）
     * 
     * @return 現在処理中の緊急通報リスト
     */
    @Cacheable(value = "emergencyCalls", key = "'active'")
    public List<EmergencyCall> findActiveCalls() {
        return emergencyCallRepository.findActiveCalls();
    }

    /**
     * 日付範囲による緊急通報の検索
     * 
     * @param startDate 開始日時
     * @param endDate   終了日時
     * @return 指定期間の緊急通報リスト
     */
    public List<EmergencyCall> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return emergencyCallRepository.findByDateRange(startDate, endDate);
    }

    /**
     * 新しい緊急通報の作成
     * 
     * 通報番号の自動生成、受信時刻の設定、最適な消防署の自動割り当て、
     * 通知の送信を行います。
     * 
     * @param emergencyCall 作成する緊急通報情報
     * @return 作成された緊急通報情報
     */
    @CacheEvict(value = "emergencyCalls", allEntries = true)
    public EmergencyCall createEmergencyCall(EmergencyCall emergencyCall) {
        emergencyCall.setReceivedAt(LocalDateTime.now());
        emergencyCall.setCallNumber(generateCallNumber());

        EmergencyCall savedCall = emergencyCallRepository.save(emergencyCall);

        // 非同期で最適な消防署を割り当て
        assignOptimalStationAsync(savedCall);

        // 非同期で通知を送信
        notificationService.sendEmergencyNotificationAsync(savedCall);

        return savedCall;
    }

    /**
     * 緊急通報の更新
     * 
     * ステータス変更に応じてタイムスタンプを自動更新します。
     * 
     * @param id            更新する緊急通報のID
     * @param emergencyCall 更新する緊急通報情報
     * @return 更新された緊急通報情報
     */
    @CacheEvict(value = "emergencyCalls", allEntries = true)
    public EmergencyCall updateEmergencyCall(Long id, EmergencyCall emergencyCall) {
        return emergencyCallRepository.findById(id)
                .map(existingCall -> {
                    existingCall.setStatus(emergencyCall.getStatus());
                    existingCall.setIncidentDescription(emergencyCall.getIncidentDescription());
                    existingCall.setPriorityLevel(emergencyCall.getPriorityLevel());

                    // ステータスに応じてタイムスタンプを更新
                    updateTimestamps(existingCall, emergencyCall.getStatus());

                    return emergencyCallRepository.save(existingCall);
                })
                .orElseThrow(() -> new RuntimeException("Emergency call not found"));
    }

    /**
     * 最適な消防署の非同期割り当て
     * 
     * 緊急通報の位置情報に基づいて、最も近い消防署を自動的に割り当てます。
     * 
     * @param emergencyCall 割り当て対象の緊急通報
     * @return 非同期処理の完了フューチャー
     */
    @Async
    public CompletableFuture<Void> assignOptimalStationAsync(EmergencyCall emergencyCall) {
        try {
            List<FireStation> nearbyStations = fireStationRepository.findStationsWithinRadius(
                    emergencyCall.getLatitude(),
                    emergencyCall.getLongitude(),
                    10.0 // 10km以内
            );

            if (!nearbyStations.isEmpty()) {
                // 最も近い消防署を選択
                FireStation optimalStation = nearbyStations.get(0);
                emergencyCall.setAssignedStation(optimalStation);
                emergencyCall.setDispatchedAt(LocalDateTime.now());
                emergencyCall.setStatus(EmergencyCall.CallStatus.DISPATCHED);

                emergencyCallRepository.save(emergencyCall);

                // 消防署に通知
                notificationService.notifyStationAsync(optimalStation, emergencyCall);
            }
        } catch (Exception e) {
            log.error("Error assigning optimal station for call: {}", emergencyCall.getCallNumber(), e);
        }

        return CompletableFuture.completedFuture(null);
    }

    private void updateTimestamps(EmergencyCall call, EmergencyCall.CallStatus newStatus) {
        switch (newStatus) {
            case DISPATCHED:
                if (call.getDispatchedAt() == null) {
                    call.setDispatchedAt(LocalDateTime.now());
                }
                break;
            case ON_SCENE:
                if (call.getArrivedAt() == null) {
                    call.setArrivedAt(LocalDateTime.now());
                }
                break;
            case CLEARED:
                if (call.getClearedAt() == null) {
                    call.setClearedAt(LocalDateTime.now());
                }
                break;
        }
    }

    private String generateCallNumber() {
        return "CALL-" + System.currentTimeMillis();
    }

    public long countByStatus(EmergencyCall.CallStatus status) {
        return emergencyCallRepository.countByStatus(status);
    }

    public List<EmergencyCall> findByAssignedStation(Long stationId) {
        return emergencyCallRepository.findByAssignedStation(stationId);
    }
}
