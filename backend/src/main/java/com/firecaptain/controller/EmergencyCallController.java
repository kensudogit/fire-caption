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

@RestController
@RequestMapping("/api/emergency-calls")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
public class EmergencyCallController {

    private final EmergencyCallService emergencyCallService;

    @GetMapping
    public ResponseEntity<Page<EmergencyCall>> getAllEmergencyCalls(Pageable pageable) {
        Page<EmergencyCall> calls = emergencyCallService.findByStatus(null, pageable);
        return ResponseEntity.ok(calls);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmergencyCall> getEmergencyCallById(@PathVariable Long id) {
        Optional<EmergencyCall> call = emergencyCallService.findById(id);
        return call.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/call-number/{callNumber}")
    public ResponseEntity<EmergencyCall> getEmergencyCallByNumber(@PathVariable String callNumber) {
        Optional<EmergencyCall> call = emergencyCallService.findByCallNumber(callNumber);
        return call.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<EmergencyCall>> getEmergencyCallsByStatus(
            @PathVariable EmergencyCall.CallStatus status, Pageable pageable) {
        Page<EmergencyCall> calls = emergencyCallService.findByStatus(status, pageable);
        return ResponseEntity.ok(calls);
    }

    @GetMapping("/active")
    public ResponseEntity<List<EmergencyCall>> getActiveCalls() {
        List<EmergencyCall> activeCalls = emergencyCallService.findActiveCalls();
        return ResponseEntity.ok(activeCalls);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<EmergencyCall>> getCallsByDateRange(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        List<EmergencyCall> calls = emergencyCallService.findByDateRange(startDate, endDate);
        return ResponseEntity.ok(calls);
    }

    @PostMapping
    public ResponseEntity<EmergencyCall> createEmergencyCall(@Valid @RequestBody EmergencyCall emergencyCall) {
        EmergencyCall createdCall = emergencyCallService.createEmergencyCall(emergencyCall);
        return ResponseEntity.ok(createdCall);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmergencyCall> updateEmergencyCall(
            @PathVariable Long id, @Valid @RequestBody EmergencyCall emergencyCall) {
        EmergencyCall updatedCall = emergencyCallService.updateEmergencyCall(id, emergencyCall);
        return ResponseEntity.ok(updatedCall);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<EmergencyCall> updateCallStatus(
            @PathVariable Long id, @RequestParam EmergencyCall.CallStatus status) {
        EmergencyCall call = new EmergencyCall();
        call.setStatus(status);
        EmergencyCall updatedCall = emergencyCallService.updateEmergencyCall(id, call);
        return ResponseEntity.ok(updatedCall);
    }

    @GetMapping("/station/{stationId}")
    public ResponseEntity<List<EmergencyCall>> getCallsByStation(@PathVariable Long stationId) {
        List<EmergencyCall> calls = emergencyCallService.findByAssignedStation(stationId);
        return ResponseEntity.ok(calls);
    }

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
        public long getPendingCount() { return pendingCount; }
        public long getDispatchedCount() { return dispatchedCount; }
        public long getOnSceneCount() { return onSceneCount; }
        public long getClearedCount() { return clearedCount; }
    }
}
