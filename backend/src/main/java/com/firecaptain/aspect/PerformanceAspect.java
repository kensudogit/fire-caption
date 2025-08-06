package com.firecaptain.aspect;

import com.firecaptain.service.PerformanceMonitoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class PerformanceAspect {

    private final PerformanceMonitoringService performanceMonitoringService;

    /**
     * 通報受付サービスのパフォーマンス監視
     */
    @Around("execution(* com.firecaptain.service.EmergencyReportService.*(..))")
    public Object monitorEmergencyReportService(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();

            // 特定のメソッドでカウンターをインクリメント
            if ("createEmergencyReport".equals(methodName)) {
                performanceMonitoringService.incrementEmergencyReportCounter();
            }

            long executionTime = System.currentTimeMillis() - startTime;
            log.debug("EmergencyReportService.{} executed in {}ms", methodName, executionTime);

            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("EmergencyReportService.{} failed after {}ms", methodName, executionTime, e);
            throw e;
        }
    }

    /**
     * 指令サービスのパフォーマンス監視
     */
    @Around("execution(* com.firecaptain.service.DispatchService.*(..))")
    public Object monitorDispatchService(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();

            // 特定のメソッドでカウンターをインクリメント
            if ("createDispatchFromReport".equals(methodName)) {
                performanceMonitoringService.incrementDispatchCounter();
            }

            long executionTime = System.currentTimeMillis() - startTime;
            log.debug("DispatchService.{} executed in {}ms", methodName, executionTime);

            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("DispatchService.{} failed after {}ms", methodName, executionTime, e);
            throw e;
        }
    }

    /**
     * 現場支援サービスのパフォーマンス監視
     */
    @Around("execution(* com.firecaptain.service.SceneSupportService.*(..))")
    public Object monitorSceneSupportService(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();

            // 特定のメソッドでカウンターをインクリメント
            if ("requestSceneSupport".equals(methodName)) {
                performanceMonitoringService.incrementSceneSupportCounter();
            }

            long executionTime = System.currentTimeMillis() - startTime;
            log.debug("SceneSupportService.{} executed in {}ms", methodName, executionTime);

            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("SceneSupportService.{} failed after {}ms", methodName, executionTime, e);
            throw e;
        }
    }

    /**
     * データベースクエリのパフォーマンス監視
     */
    @Around("execution(* com.firecaptain.repository.*.*(..))")
    public Object monitorDatabaseQueries(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        var timer = performanceMonitoringService.startDatabaseQueryTimer();

        try {
            Object result = joinPoint.proceed();
            performanceMonitoringService.stopDatabaseQueryTimer(timer);

            log.debug("Database query {}.{} completed", className, methodName);
            return result;
        } catch (Exception e) {
            performanceMonitoringService.stopDatabaseQueryTimer(timer);
            log.error("Database query {}.{} failed", className, methodName, e);
            throw e;
        }
    }

    /**
     * REST API エンドポイントのパフォーマンス監視
     */
    @Around("@annotation(org.springframework.web.bind.annotation.RequestMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public Object monitorRestEndpoints(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        var timer = performanceMonitoringService.startResponseTimeTimer();

        try {
            Object result = joinPoint.proceed();
            performanceMonitoringService.stopResponseTimeTimer(timer);

            log.debug("REST endpoint {}.{} completed", className, methodName);
            return result;
        } catch (Exception e) {
            performanceMonitoringService.stopResponseTimeTimer(timer);
            log.error("REST endpoint {}.{} failed", className, methodName, e);
            throw e;
        }
    }

    /**
     * キャッシュ操作のパフォーマンス監視
     */
    @Around("@annotation(org.springframework.cache.annotation.Cacheable) || " +
            "@annotation(org.springframework.cache.annotation.CacheEvict) || " +
            "@annotation(org.springframework.cache.annotation.CachePut)")
    public Object monitorCacheOperations(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        long startTime = System.nanoTime();

        try {
            Object result = joinPoint.proceed();

            long executionTime = TimeUnit.NANOSECONDS.toMicros(System.nanoTime() - startTime);
            log.debug("Cache operation {} completed in {}μs", methodName, executionTime);

            return result;
        } catch (Exception e) {
            long executionTime = TimeUnit.NANOSECONDS.toMicros(System.nanoTime() - startTime);
            log.error("Cache operation {} failed after {}μs", methodName, executionTime, e);
            throw e;
        }
    }

    /**
     * 非同期処理のパフォーマンス監視
     */
    @Around("@annotation(org.springframework.scheduling.annotation.Async)")
    public Object monitorAsyncOperations(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String threadName = Thread.currentThread().getName();
        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();

            long executionTime = System.currentTimeMillis() - startTime;
            log.debug("Async operation {} on thread {} completed in {}ms", methodName, threadName, executionTime);

            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("Async operation {} on thread {} failed after {}ms", methodName, threadName, executionTime, e);
            throw e;
        }
    }
}
