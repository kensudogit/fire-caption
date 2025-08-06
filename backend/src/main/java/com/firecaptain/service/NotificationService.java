package com.firecaptain.service;

import com.firecaptain.entity.EmergencyCall;
import com.firecaptain.entity.FireStation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

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

    public void sendStatusUpdate(EmergencyCall emergencyCall) {
        try {
            messagingTemplate.convertAndSend("/topic/call-updates/" + emergencyCall.getId(), 
                createStatusUpdate(emergencyCall));
        } catch (Exception e) {
            log.error("Error sending status update", e);
        }
    }

    public void sendSystemAlert(String message, String alertType) {
        try {
            messagingTemplate.convertAndSend("/topic/system-alerts", 
                createSystemAlert(message, alertType));
        } catch (Exception e) {
            log.error("Error sending system alert", e);
        }
    }

    private Object createDashboardUpdate(EmergencyCall emergencyCall) {
        return new DashboardUpdate(
            emergencyCall.getId(),
            emergencyCall.getCallNumber(),
            emergencyCall.getStatus(),
            emergencyCall.getPriorityLevel(),
            emergencyCall.getIncidentType()
        );
    }

    private Object createStatusUpdate(EmergencyCall emergencyCall) {
        return new StatusUpdate(
            emergencyCall.getId(),
            emergencyCall.getStatus(),
            emergencyCall.getDispatchedAt(),
            emergencyCall.getArrivedAt(),
            emergencyCall.getClearedAt()
        );
    }

    private Object createSystemAlert(String message, String alertType) {
        return new SystemAlert(message, alertType, System.currentTimeMillis());
    }

    // 内部クラス
    public static class DashboardUpdate {
        private Long callId;
        private String callNumber;
        private EmergencyCall.CallStatus status;
        private EmergencyCall.PriorityLevel priority;
        private EmergencyCall.IncidentType incidentType;

        public DashboardUpdate(Long callId, String callNumber, EmergencyCall.CallStatus status, 
                             EmergencyCall.PriorityLevel priority, EmergencyCall.IncidentType incidentType) {
            this.callId = callId;
            this.callNumber = callNumber;
            this.status = status;
            this.priority = priority;
            this.incidentType = incidentType;
        }

        // Getters
        public Long getCallId() { return callId; }
        public String getCallNumber() { return callNumber; }
        public EmergencyCall.CallStatus getStatus() { return status; }
        public EmergencyCall.PriorityLevel getPriority() { return priority; }
        public EmergencyCall.IncidentType getIncidentType() { return incidentType; }
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
        public Long getCallId() { return callId; }
        public EmergencyCall.CallStatus getStatus() { return status; }
        public java.time.LocalDateTime getDispatchedAt() { return dispatchedAt; }
        public java.time.LocalDateTime getArrivedAt() { return arrivedAt; }
        public java.time.LocalDateTime getClearedAt() { return clearedAt; }
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
        public String getMessage() { return message; }
        public String getAlertType() { return alertType; }
        public long getTimestamp() { return timestamp; }
    }
}
