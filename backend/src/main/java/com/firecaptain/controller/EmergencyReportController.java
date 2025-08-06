package com.firecaptain.controller;

import com.firecaptain.entity.EmergencyReport;
import com.firecaptain.service.EmergencyReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/emergency-reports")
@RequiredArgsConstructor
@Slf4j
public class EmergencyReportController {

    private final EmergencyReportService emergencyReportService;

    /**
     * 新しい通報を受付
     * POST /api/emergency-reports
     */
    @PostMapping
    public ResponseEntity<EmergencyReport> createEmergencyReport(@Valid @RequestBody EmergencyReport report) {
        log.info("Received emergency report creation request");

        EmergencyReport createdReport = emergencyReportService.createEmergencyReport(report);

        return ResponseEntity.ok(createdReport);
    }

    /**
     * 通報番号で検索
     * GET /api/emergency-reports/{reportNumber}
     */
    @GetMapping("/{reportNumber}")
    public ResponseEntity<EmergencyReport> getEmergencyReport(@PathVariable String reportNumber) {
        log.info("Searching for emergency report: {}", reportNumber);

        Optional<EmergencyReport> report = emergencyReportService.findByReportNumber(reportNumber);

        return report.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * ステータスで検索
     * GET /api/emergency-reports/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<EmergencyReport>> getEmergencyReportsByStatus(
            @PathVariable EmergencyReport.ReportStatus status) {
        log.info("Searching for emergency reports with status: {}", status);

        List<EmergencyReport> reports = emergencyReportService.findByStatus(status);

        return ResponseEntity.ok(reports);
    }

    /**
     * 緊急度で検索
     * GET /api/emergency-reports/priority/{priorityLevel}
     */
    @GetMapping("/priority/{priorityLevel}")
    public ResponseEntity<List<EmergencyReport>> getEmergencyReportsByPriority(
            @PathVariable EmergencyReport.PriorityLevel priorityLevel) {
        log.info("Searching for emergency reports with priority: {}", priorityLevel);

        List<EmergencyReport> reports = emergencyReportService.findByPriorityLevel(priorityLevel);

        return ResponseEntity.ok(reports);
    }

    /**
     * 通報を更新
     * PUT /api/emergency-reports/{reportNumber}
     */
    @PutMapping("/{reportNumber}")
    public ResponseEntity<EmergencyReport> updateEmergencyReport(@PathVariable String reportNumber,
            @Valid @RequestBody EmergencyReport report) {
        log.info("Updating emergency report: {}", reportNumber);

        // 通報番号を設定
        report.setReportNumber(reportNumber);

        EmergencyReport updatedReport = emergencyReportService.updateEmergencyReport(report);

        return ResponseEntity.ok(updatedReport);
    }

    /**
     * 通報ステータスを更新
     * PATCH /api/emergency-reports/{reportNumber}/status
     */
    @PatchMapping("/{reportNumber}/status")
    public ResponseEntity<EmergencyReport> updateEmergencyReportStatus(
            @PathVariable String reportNumber,
            @RequestBody StatusUpdateRequest request) {
        log.info("Updating emergency report status: {} -> {}", reportNumber, request.status());

        EmergencyReport updatedReport = emergencyReportService.updateStatus(reportNumber, request.status());

        return ResponseEntity.ok(updatedReport);
    }

    /**
     * 統計情報を取得
     * GET /api/emergency-reports/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<EmergencyReportService.EmergencyReportStatistics> getStatistics() {
        log.info("Getting emergency report statistics");

        EmergencyReportService.EmergencyReportStatistics statistics = emergencyReportService.getStatistics();

        return ResponseEntity.ok(statistics);
    }

    /**
     * ステータス更新リクエスト
     */
    public record StatusUpdateRequest(EmergencyReport.ReportStatus status) {
    }
}
