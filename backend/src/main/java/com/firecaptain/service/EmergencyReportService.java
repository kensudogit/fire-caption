package com.firecaptain.service;

import com.firecaptain.entity.EmergencyReport;
import com.firecaptain.repository.EmergencyReportRepository;
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
public class EmergencyReportService {
    
    private final EmergencyReportRepository emergencyReportRepository;
    private final DispatchService dispatchService;
    
    /**
     * 新しい通報を受付
     */
    @CacheEvict(value = "emergencyReports", allEntries = true)
    public EmergencyReport createEmergencyReport(EmergencyReport report) {
        log.info("Creating new emergency report: {}", report.getReportNumber());
        
        // 通報番号を生成
        report.setReportNumber(generateReportNumber());
        report.setReceivedAt(LocalDateTime.now());
        report.setStatus(EmergencyReport.ReportStatus.RECEIVED);
        
        EmergencyReport savedReport = emergencyReportRepository.save(report);
        
        // 非同期で指令処理を開始
        processDispatchAsync(savedReport);
        
        return savedReport;
    }
    
    /**
     * 通報番号で検索
     */
    @Cacheable(value = "emergencyReports", key = "#reportNumber")
    public Optional<EmergencyReport> findByReportNumber(String reportNumber) {
        log.debug("Finding emergency report by number: {}", reportNumber);
        return emergencyReportRepository.findByReportNumber(reportNumber);
    }
    
    /**
     * ステータスで検索
     */
    public List<EmergencyReport> findByStatus(EmergencyReport.ReportStatus status) {
        log.debug("Finding emergency reports by status: {}", status);
        return emergencyReportRepository.findByStatus(status);
    }
    
    /**
     * 緊急度で検索
     */
    public List<EmergencyReport> findByPriorityLevel(EmergencyReport.PriorityLevel priorityLevel) {
        log.debug("Finding emergency reports by priority: {}", priorityLevel);
        return emergencyReportRepository.findByPriorityLevel(priorityLevel);
    }
    
    /**
     * 通報を更新
     */
    @CacheEvict(value = "emergencyReports", key = "#report.reportNumber")
    public EmergencyReport updateEmergencyReport(EmergencyReport report) {
        log.info("Updating emergency report: {}", report.getReportNumber());
        return emergencyReportRepository.save(report);
    }
    
    /**
     * 通報ステータスを更新
     */
    @CacheEvict(value = "emergencyReports", key = "#reportNumber")
    public EmergencyReport updateStatus(String reportNumber, EmergencyReport.ReportStatus status) {
        log.info("Updating emergency report status: {} -> {}", reportNumber, status);
        
        EmergencyReport report = emergencyReportRepository.findByReportNumber(reportNumber)
                .orElseThrow(() -> new RuntimeException("Emergency report not found: " + reportNumber));
        
        report.setStatus(status);
        
        // ステータスに応じてタイムスタンプを更新
        switch (status) {
            case DISPATCHED -> report.setDispatchedAt(LocalDateTime.now());
            case ON_SCENE -> report.setArrivedAt(LocalDateTime.now());
            case COMPLETED -> report.setCompletedAt(LocalDateTime.now());
        }
        
        return emergencyReportRepository.save(report);
    }
    
    /**
     * 非同期で指令処理を実行
     */
    @Async
    public void processDispatchAsync(EmergencyReport report) {
        try {
            log.info("Processing dispatch for emergency report: {}", report.getReportNumber());
            dispatchService.createDispatchFromReport(report);
        } catch (Exception e) {
            log.error("Error processing dispatch for report: {}", report.getReportNumber(), e);
        }
    }
    
    /**
     * 通報番号を生成
     */
    private String generateReportNumber() {
        String timestamp = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "ER-" + timestamp + "-" + random;
    }
    
    /**
     * 統計情報を取得
     */
    public EmergencyReportStatistics getStatistics() {
        log.debug("Getting emergency report statistics");
        
        long totalReports = emergencyReportRepository.count();
        long activeReports = emergencyReportRepository.countByStatusIn(
                List.of(EmergencyReport.ReportStatus.RECEIVED, EmergencyReport.ReportStatus.DISPATCHED, EmergencyReport.ReportStatus.EN_ROUTE, EmergencyReport.ReportStatus.ON_SCENE)
        );
        
        return EmergencyReportStatistics.builder()
                .totalReports(totalReports)
                .activeReports(activeReports)
                .build();
    }
    
    /**
     * 統計情報クラス
     */
    public static class EmergencyReportStatistics {
        private final long totalReports;
        private final long activeReports;
        
        public EmergencyReportStatistics(long totalReports, long activeReports) {
            this.totalReports = totalReports;
            this.activeReports = activeReports;
        }
        
        public long getTotalReports() { return totalReports; }
        public long getActiveReports() { return activeReports; }
        
        public static EmergencyReportStatisticsBuilder builder() {
            return new EmergencyReportStatisticsBuilder();
        }
        
        public static class EmergencyReportStatisticsBuilder {
            private long totalReports;
            private long activeReports;
            
            public EmergencyReportStatisticsBuilder totalReports(long totalReports) {
                this.totalReports = totalReports;
                return this;
            }
            
            public EmergencyReportStatisticsBuilder activeReports(long activeReports) {
                this.activeReports = activeReports;
                return this;
            }
            
            public EmergencyReportStatistics build() {
                return new EmergencyReportStatistics(totalReports, activeReports);
            }
        }
    }
}
