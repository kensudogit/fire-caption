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

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EmergencyCallService {

    private final EmergencyCallRepository emergencyCallRepository;
    private final FireStationRepository fireStationRepository;
    private final FirefighterRepository firefighterRepository;
    private final NotificationService notificationService;

    @Cacheable(value = "emergencyCalls", key = "#id")
    public Optional<EmergencyCall> findById(Long id) {
        return emergencyCallRepository.findById(id);
    }

    @Cacheable(value = "emergencyCalls", key = "#callNumber")
    public Optional<EmergencyCall> findByCallNumber(String callNumber) {
        return emergencyCallRepository.findByCallNumber(callNumber);
    }

    public Page<EmergencyCall> findByStatus(EmergencyCall.CallStatus status, Pageable pageable) {
        return emergencyCallRepository.findByStatus(status, pageable);
    }

    @Cacheable(value = "emergencyCalls", key = "'active'")
    public List<EmergencyCall> findActiveCalls() {
        return emergencyCallRepository.findActiveCalls();
    }

    public List<EmergencyCall> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return emergencyCallRepository.findByDateRange(startDate, endDate);
    }

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
