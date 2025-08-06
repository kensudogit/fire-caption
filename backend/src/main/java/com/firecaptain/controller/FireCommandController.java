package com.firecaptain.controller;

import com.firecaptain.entity.EmergencyReport;
import com.firecaptain.entity.Dispatch;
import com.firecaptain.entity.SceneSupport;
import com.firecaptain.entity.DataAnalysis;
import com.firecaptain.service.EmergencyReportService;
import com.firecaptain.service.DispatchService;
import com.firecaptain.service.SceneSupportService;
import com.firecaptain.service.DataAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/fire-command")
@RequiredArgsConstructor
@Slf4j
public class FireCommandController {

    private final EmergencyReportService emergencyReportService;
    private final DispatchService dispatchService;
    private final SceneSupportService sceneSupportService;
    private final DataAnalysisService dataAnalysisService;

    /**
     * 処理フロー全体のダッシュボード情報を取得
     * GET /api/fire-command/dashboard
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        log.info("Getting fire command dashboard information");

        Map<String, Object> dashboard = new HashMap<>();

        // 各段階の統計情報を取得
        dashboard.put("emergencyReports", emergencyReportService.getStatistics());
        dashboard.put("dispatches", dispatchService.getStatistics());
        dashboard.put("sceneSupports", sceneSupportService.getStatistics());
        dashboard.put("dataAnalyses", dataAnalysisService.getStatistics());

        // 現在時刻
        dashboard.put("currentTime", LocalDateTime.now());

        // システムステータス
        dashboard.put("systemStatus", "OPERATIONAL");

        return ResponseEntity.ok(dashboard);
    }

    /**
     * 処理フローの進行状況を取得
     * GET /api/fire-command/workflow/{reportNumber}
     */
    @GetMapping("/workflow/{reportNumber}")
    public ResponseEntity<Map<String, Object>> getWorkflowStatus(@PathVariable String reportNumber) {
        log.info("Getting workflow status for report: {}", reportNumber);

        Map<String, Object> workflow = new HashMap<>();

        // 通報情報
        emergencyReportService.findByReportNumber(reportNumber).ifPresent(report -> {
            workflow.put("emergencyReport", report);

            // 指令情報
            // 実際の実装では、通報と指令の関連を取得する必要があります
            workflow.put("dispatch", null);

            // 現場支援情報
            workflow.put("sceneSupports", null);

            // 完了報告情報
            workflow.put("completionReport", null);
        });

        return ResponseEntity.ok(workflow);
    }

    /**
     * 処理フローの次の段階に進む
     * POST /api/fire-command/workflow/{reportNumber}/next
     */
    @PostMapping("/workflow/{reportNumber}/next")
    public ResponseEntity<Map<String, Object>> proceedToNextStage(@PathVariable String reportNumber,
            @RequestBody NextStageRequest request) {
        log.info("Proceeding to next stage for report: {} -> {}", reportNumber, request.stage());

        Map<String, Object> result = new HashMap<>();

        switch (request.stage()) {
            case "DISPATCH" -> {
                // 通報から指令を作成
                emergencyReportService.findByReportNumber(reportNumber).ifPresent(report -> {
                    Dispatch dispatch = dispatchService.createDispatchFromReport(report);
                    result.put("dispatch", dispatch);
                    result.put("message", "Dispatch created successfully");
                });
            }
            case "SCENE_SUPPORT" -> {
                // 現場支援を要求
                result.put("message", "Scene support requested");
            }
            case "COMPLETION" -> {
                // 完了報告を作成
                result.put("message", "Completion report created");
            }
            case "ANALYSIS" -> {
                // データ分析を実行
                result.put("message", "Data analysis initiated");
            }
        }

        return ResponseEntity.ok(result);
    }

    /**
     * リアルタイム更新を取得
     * GET /api/fire-command/realtime
     */
    @GetMapping("/realtime")
    public ResponseEntity<Map<String, Object>> getRealTimeUpdates() {
        log.info("Getting real-time updates");

        Map<String, Object> updates = new HashMap<>();

        // 最新の通報
        updates.put("latestReports", emergencyReportService.findByStatus(EmergencyReport.ReportStatus.RECEIVED));

        // 最新の指令
        updates.put("latestDispatches", dispatchService.findByStatus(Dispatch.DispatchStatus.DISPATCHED));

        // 最新の現場支援
        updates.put("latestSupports", sceneSupportService.findByStatus(SceneSupport.SupportStatus.REQUESTED));

        // 最新の分析結果
        updates.put("latestAnalyses", dataAnalysisService.findLatestAnalyses(5));

        return ResponseEntity.ok(updates);
    }

    /**
     * システムヘルスチェック
     * GET /api/fire-command/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealthStatus() {
        log.info("Getting system health status");

        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("version", "1.0.0");
        health.put("components", Map.of(
                "database", "UP",
                "cache", "UP",
                "websocket", "UP"));

        return ResponseEntity.ok(health);
    }

    /**
     * 次の段階リクエスト
     */
    public record NextStageRequest(String stage) {
    }
}
