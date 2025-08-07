package com.firecaptain.service;

import com.firecaptain.entity.EmergencyCall;
import com.firecaptain.entity.FireStation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * 通知サービス
 * 
 * 消防司令システムのリアルタイム通知機能を担当します。
 * WebSocketを使用して緊急通報、ステータス更新、システムアラートなどの
 * 通知をフロントエンドに送信します。
 * 
 * @author FireCaptain Team
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 緊急通報の非同期通知送信
     * 
     * 新しい緊急通報が発生した際に、WebSocketを通じて
     * リアルタイムで通知を送信します。
     * 
     * @param emergencyCall 通知対象の緊急通報
     * @return 非同期処理の完了フューチャー
     */
    @Async
    public CompletableFuture<Void> sendEmergencyNotificationAsync(EmergencyCall emergencyCall) {
        try {
            // WebSocketを通じてリアルタイム通知を送信
            messagingTemplate.convertAndSend("/topic/emergency-calls", emergencyCall);

            // ダッシュボード更新通知
            messagingTemplate.convertAndSend("/topic/dashboard-updates",
                    createDashboardUpdate(emergencyCall));

            log.info("Emergency notification sent for call: {}", emergencyCall.getCallNumber());
        } catch (Exception e) {
            log.error("Error sending emergency notification", e);
        }

        return CompletableFuture.completedFuture(null);
    }

    /**
     * 消防署への非同期通知送信
     * 
     * 特定の消防署に対して緊急通報の通知を送信します。
     * 
     * @param station       通知対象の消防署
     * @param emergencyCall 緊急通報情報
     * @return 非同期処理の完了フューチャー
     */
    @Async
    public CompletableFuture<Void> notifyStationAsync(FireStation station, EmergencyCall emergencyCall) {
        try {
            // 特定の消防署に通知
            messagingTemplate.convertAndSend("/topic/station/" + station.getId(), emergencyCall);

            log.info("Station notification sent to station: {}", station.getStationCode());
        } catch (Exception e) {
            log.error("Error sending station notification", e);
        }

        return CompletableFuture.completedFuture(null);
    }

    /**
     * ステータス更新通知の送信
     * 
     * 緊急通報のステータスが変更された際に通知を送信します。
     * 
     * @param emergencyCall ステータスが更新された緊急通報
     */
    public void sendStatusUpdate(EmergencyCall emergencyCall) {
        try {
            messagingTemplate.convertAndSend("/topic/call-updates/" + emergencyCall.getId(),
                    createStatusUpdate(emergencyCall));
        } catch (Exception e) {
            log.error("Error sending status update", e);
        }
    }

    /**
     * システムアラートの送信
     * 
     * システム全体に関する重要なアラートを送信します。
     * 
     * @param message   アラートメッセージ
     * @param alertType アラートの種類
     */
    public void sendSystemAlert(String message, String alertType) {
        try {
            messagingTemplate.convertAndSend("/topic/system-alerts",
                    createSystemAlert(message, alertType));
        } catch (Exception e) {
            log.error("Error sending system alert", e);
        }
    }

    /**
     * ダッシュボード更新情報の作成
     * 
     * @param emergencyCall 緊急通報情報
     * @return ダッシュボード更新オブジェクト
     */
    private Object createDashboardUpdate(EmergencyCall emergencyCall) {
        return new DashboardUpdate(
                emergencyCall.getId(),
                emergencyCall.getCallNumber(),
                emergencyCall.getStatus(),
                emergencyCall.getPriorityLevel(),
                emergencyCall.getIncidentType());
    }

    /**
     * ステータス更新情報の作成
     * 
     * @param emergencyCall 緊急通報情報
     * @return ステータス更新オブジェクト
     */
    private Object createStatusUpdate(EmergencyCall emergencyCall) {
        return new StatusUpdate(
                emergencyCall.getId(),
                emergencyCall.getStatus(),
                emergencyCall.getDispatchedAt(),
                emergencyCall.getArrivedAt(),
                emergencyCall.getClearedAt());
    }

    /**
     * システムアラート情報の作成
     * 
     * @param message   アラートメッセージ
     * @param alertType アラートの種類
     * @return システムアラートオブジェクト
     */
    private Object createSystemAlert(String message, String alertType) {
        return new SystemAlert(message, alertType, System.currentTimeMillis());
    }

    /**
     * ダッシュボード更新情報クラス
     * 
     * ダッシュボードの更新に必要な情報を格納します。
     */
    public static class DashboardUpdate {
        private Long callId; // 通報ID
        private String callNumber; // 通報番号
        private EmergencyCall.CallStatus status; // 通報ステータス
        private EmergencyCall.PriorityLevel priority; // 優先度
        private EmergencyCall.IncidentType incidentType; // 事故事象の種類

        public DashboardUpdate(Long callId, String callNumber, EmergencyCall.CallStatus status,
                EmergencyCall.PriorityLevel priority, EmergencyCall.IncidentType incidentType) {
            this.callId = callId;
            this.callNumber = callNumber;
            this.status = status;
            this.priority = priority;
            this.incidentType = incidentType;
        }

        // Getters
        public Long getCallId() {
            return callId;
        }

        public String getCallNumber() {
            return callNumber;
        }

        public EmergencyCall.CallStatus getStatus() {
            return status;
        }

        public EmergencyCall.PriorityLevel getPriority() {
            return priority;
        }

        public EmergencyCall.IncidentType getIncidentType() {
            return incidentType;
        }
    }

    public static class StatusUpdate {
        private Long callId;
        private EmergencyCall.CallStatus status;
        private java.time.LocalDateTime dispatchedAt;
        private java.time.LocalDateTime arrivedAt;
        private java.time.LocalDateTime clearedAt;

        public StatusUpdate(Long callId, EmergencyCall.CallStatus status,
                java.time.LocalDateTime dispatchedAt, java.time.LocalDateTime arrivedAt,
                java.time.LocalDateTime clearedAt) {
            this.callId = callId;
            this.status = status;
            this.dispatchedAt = dispatchedAt;
            this.arrivedAt = arrivedAt;
            this.clearedAt = clearedAt;
        }

        // Getters
        public Long getCallId() {
            return callId;
        }

        public EmergencyCall.CallStatus getStatus() {
            return status;
        }

        public java.time.LocalDateTime getDispatchedAt() {
            return dispatchedAt;
        }

        public java.time.LocalDateTime getArrivedAt() {
            return arrivedAt;
        }

        public java.time.LocalDateTime getClearedAt() {
            return clearedAt;
        }
    }

    public static class SystemAlert {
        private String message;
        private String alertType;
        private long timestamp;

        public SystemAlert(String message, String alertType, long timestamp) {
            this.message = message;
            this.alertType = alertType;
            this.timestamp = timestamp;
        }

        // Getters
        public String getMessage() {
            return message;
        }

        public String getAlertType() {
            return alertType;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}
