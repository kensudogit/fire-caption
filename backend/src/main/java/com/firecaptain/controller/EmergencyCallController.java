package com.firecaptain.controller;

import com.firecaptain.entity.EmergencyCall;
import com.firecaptain.service.EmergencyCallService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 緊急通報管理コントローラー
 * 
 * 緊急通報の作成、取得、更新、削除などのREST APIエンドポイントを提供します。
 * 消防司令システムのフロントエンドから緊急通報情報を管理するための
 * インターフェースとして機能します。
 * 
 * @author FireCaptain Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/emergency-calls")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
public class EmergencyCallController {

    private final EmergencyCallService emergencyCallService;

    /**
     * すべての緊急通報をページネーション付きで取得
     * 
     * @param pageable ページネーション情報
     * @return 緊急通報のページ
     */
    @GetMapping
    public ResponseEntity<Page<EmergencyCall>> getAllEmergencyCalls(Pageable pageable) {
        Page<EmergencyCall> calls = emergencyCallService.findByStatus(null, pageable);
        return ResponseEntity.ok(calls);
    }

    /**
     * IDによる緊急通報の取得
     * 
     * @param id 緊急通報のID
     * @return 緊急通報情報（存在しない場合は404）
     */
    @GetMapping("/{id}")
    public ResponseEntity<EmergencyCall> getEmergencyCallById(@PathVariable Long id) {
        Optional<EmergencyCall> call = emergencyCallService.findById(id);
        return call.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 通報番号による緊急通報の取得
     * 
     * @param callNumber 通報番号
     * @return 緊急通報情報（存在しない場合は404）
     */
    @GetMapping("/call-number/{callNumber}")
    public ResponseEntity<EmergencyCall> getEmergencyCallByNumber(@PathVariable String callNumber) {
        Optional<EmergencyCall> call = emergencyCallService.findByCallNumber(callNumber);
        return call.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * ステータスによる緊急通報の取得
     * 
     * @param status   通報ステータス
     * @param pageable ページネーション情報
     * @return 指定ステータスの緊急通報のページ
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<EmergencyCall>> getEmergencyCallsByStatus(
            @PathVariable EmergencyCall.CallStatus status, Pageable pageable) {
        Page<EmergencyCall> calls = emergencyCallService.findByStatus(status, pageable);
        return ResponseEntity.ok(calls);
    }

    /**
     * アクティブな緊急通報の取得
     * 
     * @return 現在処理中の緊急通報リスト
     */
    @GetMapping("/active")
    public ResponseEntity<List<EmergencyCall>> getActiveCalls() {
        List<EmergencyCall> activeCalls = emergencyCallService.findActiveCalls();
        return ResponseEntity.ok(activeCalls);
    }

    /**
     * 日付範囲による緊急通報の取得
     * 
     * @param startDate 開始日時
     * @param endDate   終了日時
     * @return 指定期間の緊急通報リスト
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<EmergencyCall>> getCallsByDateRange(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        List<EmergencyCall> calls = emergencyCallService.findByDateRange(startDate, endDate);
        return ResponseEntity.ok(calls);
    }

    /**
     * 新しい緊急通報の作成
     * 
     * @param emergencyCall 作成する緊急通報情報
     * @return 作成された緊急通報情報
     */
    @PostMapping
    public ResponseEntity<EmergencyCall> createEmergencyCall(@Valid @RequestBody EmergencyCall emergencyCall) {
        EmergencyCall createdCall = emergencyCallService.createEmergencyCall(emergencyCall);
        return ResponseEntity.ok(createdCall);
    }

    /**
     * 緊急通報の更新
     * 
     * @param id            更新する緊急通報のID
     * @param emergencyCall 更新する緊急通報情報
     * @return 更新された緊急通報情報
     */
    @PutMapping("/{id}")
    public ResponseEntity<EmergencyCall> updateEmergencyCall(
            @PathVariable Long id, @Valid @RequestBody EmergencyCall emergencyCall) {
        EmergencyCall updatedCall = emergencyCallService.updateEmergencyCall(id, emergencyCall);
        return ResponseEntity.ok(updatedCall);
    }

    /**
     * 緊急通報ステータスの更新
     * 
     * @param id     更新する緊急通報のID
     * @param status 新しいステータス
     * @return 更新された緊急通報情報
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<EmergencyCall> updateCallStatus(
            @PathVariable Long id, @RequestParam EmergencyCall.CallStatus status) {
        EmergencyCall call = new EmergencyCall();
        call.setStatus(status);
        EmergencyCall updatedCall = emergencyCallService.updateEmergencyCall(id, call);
        return ResponseEntity.ok(updatedCall);
    }

    /**
     * 消防署別の緊急通報取得
     * 
     * @param stationId 消防署ID
     * @return 指定消防署の緊急通報リスト
     */
    @GetMapping("/station/{stationId}")
    public ResponseEntity<List<EmergencyCall>> getCallsByStation(@PathVariable Long stationId) {
        List<EmergencyCall> calls = emergencyCallService.findByAssignedStation(stationId);
        return ResponseEntity.ok(calls);
    }

    /**
     * ステータス別の緊急通報件数取得
     * 
     * @param status ステータス
     * @return 指定ステータスの緊急通報件数
     */
    @GetMapping("/stats/status/{status}")
    public ResponseEntity<Long> getCallCountByStatus(@PathVariable EmergencyCall.CallStatus status) {
        long count = emergencyCallService.countByStatus(status);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/summary")
    public ResponseEntity<CallSummary> getCallSummary() {
        long pendingCount = emergencyCallService.countByStatus(EmergencyCall.CallStatus.PENDING);
        long dispatchedCount = emergencyCallService.countByStatus(EmergencyCall.CallStatus.DISPATCHED);
        long onSceneCount = emergencyCallService.countByStatus(EmergencyCall.CallStatus.ON_SCENE);
        long clearedCount = emergencyCallService.countByStatus(EmergencyCall.CallStatus.CLEARED);

        CallSummary summary = new CallSummary(pendingCount, dispatchedCount, onSceneCount, clearedCount);
        return ResponseEntity.ok(summary);
    }

    public static class CallSummary {
        private long pendingCount;
        private long dispatchedCount;
        private long onSceneCount;
        private long clearedCount;

        public CallSummary(long pendingCount, long dispatchedCount, long onSceneCount, long clearedCount) {
            this.pendingCount = pendingCount;
            this.dispatchedCount = dispatchedCount;
            this.onSceneCount = onSceneCount;
            this.clearedCount = clearedCount;
        }

        // Getters
        public long getPendingCount() {
            return pendingCount;
        }

        public long getDispatchedCount() {
            return dispatchedCount;
        }

        public long getOnSceneCount() {
            return onSceneCount;
        }

        public long getClearedCount() {
            return clearedCount;
        }
    }
}
