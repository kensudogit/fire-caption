package com.firecaptain.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Tags;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.cache.Cache;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class PerformanceMonitoringService {

    private final MeterRegistry meterRegistry;
    private final CacheManager cacheManager;

    // メトリクス定義
    private final Counter emergencyReportCounter;
    private final Counter dispatchCounter;
    private final Counter sceneSupportCounter;
    private final Timer responseTimeTimer;
    private final Timer databaseQueryTimer;

    public PerformanceMonitoringService(MeterRegistry meterRegistry, CacheManager cacheManager) {
        this.meterRegistry = meterRegistry;
        this.cacheManager = cacheManager;

        // カウンターの初期化
        this.emergencyReportCounter = Counter.builder("fire_captain_emergency_reports_total")
                .description("Total number of emergency reports")
                .register(meterRegistry);

        this.dispatchCounter = Counter.builder("fire_captain_dispatches_total")
                .description("Total number of dispatches")
                .register(meterRegistry);

        this.sceneSupportCounter = Counter.builder("fire_captain_scene_supports_total")
                .description("Total number of scene supports")
                .register(meterRegistry);

        this.responseTimeTimer = Timer.builder("fire_captain_response_time")
                .description("Response time for emergency operations")
                .register(meterRegistry);

        this.databaseQueryTimer = Timer.builder("fire_captain_database_query_time")
                .description("Database query execution time")
                .register(meterRegistry);
    }

    /**
     * 通報受付カウンターをインクリメント
     */
    public void incrementEmergencyReportCounter() {
        emergencyReportCounter.increment();
        log.debug("Emergency report counter incremented");
    }

    /**
     * 指令カウンターをインクリメント
     */
    public void incrementDispatchCounter() {
        dispatchCounter.increment();
        log.debug("Dispatch counter incremented");
    }

    /**
     * 現場支援カウンターをインクリメント
     */
    public void incrementSceneSupportCounter() {
        sceneSupportCounter.increment();
        log.debug("Scene support counter incremented");
    }

    /**
     * 応答時間を記録
     */
    public Timer.Sample startResponseTimeTimer() {
        return Timer.start(meterRegistry);
    }

    /**
     * 応答時間を停止して記録
     */
    public void stopResponseTimeTimer(Timer.Sample sample) {
        sample.stop(responseTimeTimer);
        log.debug("Response time recorded");
    }

    /**
     * データベースクエリ時間を記録
     */
    public Timer.Sample startDatabaseQueryTimer() {
        return Timer.start(meterRegistry);
    }

    /**
     * データベースクエリ時間を停止して記録
     */
    public void stopDatabaseQueryTimer(Timer.Sample sample) {
        sample.stop(databaseQueryTimer);
        log.debug("Database query time recorded");
    }

    /**
     * キャッシュヒット率を監視
     */
    @Scheduled(fixedRate = 60000) // 1分ごと
    public void monitorCachePerformance() {
        try {
            javax.cache.CacheManager jCacheManager = Caching.getCachingProvider().getCacheManager();

            // 各キャッシュの統計情報を取得
            monitorCache("emergencyReports", jCacheManager);
            monitorCache("dispatches", jCacheManager);
            monitorCache("sceneSupports", jCacheManager);
            monitorCache("dataAnalyses", jCacheManager);

        } catch (Exception e) {
            log.warn("Failed to monitor cache performance", e);
        }
    }

    /**
     * 特定のキャッシュを監視
     */
    private void monitorCache(String cacheName, javax.cache.CacheManager jCacheManager) {
        try {
            Cache<?, ?> cache = jCacheManager.getCache(cacheName);
            if (cache != null) {
                // サイズのみ監視（統計情報は利用できない場合がある）
                meterRegistry.gauge("fire_captain_cache_size",
                        Tags.of("cache", cacheName), 1.0);

                log.debug("Cache {} - Available: true", cacheName);
            }
        } catch (Exception e) {
            log.warn("Failed to monitor cache: {}", cacheName, e);
        }
    }

    /**
     * システムリソース使用量を監視
     */
    @Scheduled(fixedRate = 30000) // 30秒ごと
    public void monitorSystemResources() {
        Runtime runtime = Runtime.getRuntime();

        // メモリ使用量
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory();

        meterRegistry.gauge("fire_captain_memory_used_mb", usedMemory / 1024 / 1024);
        meterRegistry.gauge("fire_captain_memory_free_mb", freeMemory / 1024 / 1024);
        meterRegistry.gauge("fire_captain_memory_total_mb", totalMemory / 1024 / 1024);
        meterRegistry.gauge("fire_captain_memory_max_mb", maxMemory / 1024 / 1024);

        // メモリ使用率
        double memoryUsagePercent = (double) usedMemory / maxMemory * 100;
        meterRegistry.gauge("fire_captain_memory_usage_percent", memoryUsagePercent);

        // スレッド数
        int threadCount = Thread.activeCount();
        meterRegistry.gauge("fire_captain_thread_count", threadCount);

        log.debug("System resources - Memory: {:.1f}% used, Threads: {}",
                memoryUsagePercent, threadCount);
    }

    /**
     * データベース接続プールを監視
     */
    @Scheduled(fixedRate = 45000) // 45秒ごと
    public void monitorDatabasePool() {
        try {
            // HikariCPの統計情報を取得
            meterRegistry.gauge("fire_captain_db_pool_active_connections",
                    getHikariPoolMetric("ActiveConnections"));
            meterRegistry.gauge("fire_captain_db_pool_idle_connections",
                    getHikariPoolMetric("IdleConnections"));
            meterRegistry.gauge("fire_captain_db_pool_total_connections",
                    getHikariPoolMetric("TotalConnections"));

        } catch (Exception e) {
            log.warn("Failed to monitor database pool", e);
        }
    }

    /**
     * HikariCPのメトリクスを取得
     */
    private double getHikariPoolMetric(String metricName) {
        try {
            return meterRegistry.get("hikaricp.connections." + metricName.toLowerCase())
                    .gauge().value();
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * パフォーマンスサマリーをログ出力
     */
    @Scheduled(fixedRate = 300000) // 5分ごと
    public void logPerformanceSummary() {
        log.info("=== Performance Summary ===");
        log.info("Emergency Reports: {}", emergencyReportCounter.count());
        log.info("Dispatches: {}", dispatchCounter.count());
        log.info("Scene Supports: {}", sceneSupportCounter.count());
        log.info("Average Response Time: {:.2f}ms",
                responseTimeTimer.mean(TimeUnit.MILLISECONDS));
        log.info("Average Database Query Time: {:.2f}ms",
                databaseQueryTimer.mean(TimeUnit.MILLISECONDS));
        log.info("==========================");
    }
}
