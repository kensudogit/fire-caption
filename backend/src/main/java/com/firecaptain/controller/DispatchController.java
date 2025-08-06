package com.firecaptain.controller;

import com.firecaptain.entity.Dispatch;
import com.firecaptain.service.DispatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/dispatches")
@RequiredArgsConstructor
@Slf4j
public class DispatchController {
    
    private final DispatchService dispatchService;
    
    /**
     * 指令番号で検索
     * GET /api/dispatches/{dispatchNumber}
     */
    @GetMapping("/{dispatchNumber}")
    public ResponseEntity<Dispatch> getDispatch(@PathVariable String dispatchNumber) {
        log.info("Searching for dispatch: {}", dispatchNumber);
        
        Optional<Dispatch> dispatch = dispatchService.findByDispatchNumber(dispatchNumber);
        
        return dispatch.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * ステータスで検索
     * GET /api/dispatches/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Dispatch>> getDispatchesByStatus(@PathVariable Dispatch.DispatchStatus status) {
        log.info("Searching for dispatches with status: {}", status);
        
        List<Dispatch> dispatches = dispatchService.findByStatus(status);
        
        return ResponseEntity.ok(dispatches);
    }
    
    /**
     * 指令を更新
     * PUT /api/dispatches/{dispatchNumber}
     */
    @PutMapping("/{dispatchNumber}")
    public ResponseEntity<Dispatch> updateDispatch(@PathVariable String dispatchNumber, @Valid @RequestBody Dispatch dispatch) {
        log.info("Updating dispatch: {}", dispatchNumber);
        
        // 指令番号を設定
        dispatch.setDispatchNumber(dispatchNumber);
        
        Dispatch updatedDispatch = dispatchService.updateDispatch(dispatch);
        
        return ResponseEntity.ok(updatedDispatch);
    }
    
    /**
     * 指令ステータスを更新
     * PATCH /api/dispatches/{dispatchNumber}/status
     */
    @PatchMapping("/{dispatchNumber}/status")
    public ResponseEntity<Dispatch> updateDispatchStatus(
            @PathVariable String dispatchNumber,
            @RequestBody StatusUpdateRequest request) {
        log.info("Updating dispatch status: {} -> {}", dispatchNumber, request.status());
        
        Dispatch updatedDispatch = dispatchService.updateStatus(dispatchNumber, request.status());
        
        return ResponseEntity.ok(updatedDispatch);
    }
    
    /**
     * 統計情報を取得
     * GET /api/dispatches/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<DispatchService.DispatchStatistics> getStatistics() {
        log.info("Getting dispatch statistics");
        
        DispatchService.DispatchStatistics statistics = dispatchService.getStatistics();
        
        return ResponseEntity.ok(statistics);
    }
    
    /**
     * ステータス更新リクエスト
     */
    public record StatusUpdateRequest(Dispatch.DispatchStatus status) {}
}
