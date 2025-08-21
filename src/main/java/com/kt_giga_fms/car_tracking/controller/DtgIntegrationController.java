package com.kt_giga_fms.car_tracking.controller;

import com.kt_giga_fms.car_tracking.dto.ApiResponse;
import com.kt_giga_fms.car_tracking.dto.TripStartRequest;
import com.kt_giga_fms.car_tracking.dto.TripEndRequest;
import com.kt_giga_fms.car_tracking.dto.TrackingData;
import com.kt_giga_fms.car_tracking.service.DtgIntegrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1/tracking")
@RequiredArgsConstructor
public class DtgIntegrationController {
    
    private final DtgIntegrationService dtgIntegrationService;
    
    /**
     * DTG 서비스에서 운행 시작 알림을 받음
     */
    @PostMapping("/trips/start")
    public ResponseEntity<ApiResponse<String>> handleTripStarted(@Valid @RequestBody TripStartRequest request) {
        log.info("DTG에서 운행 시작 알림 수신: 차량={}, 운전자={}", request.getVehicleId(), request.getDriverId());
        
        try {
            dtgIntegrationService.handleTripStarted(request);
            return ResponseEntity.ok(ApiResponse.success("운행 시작이 처리되었습니다."));
        } catch (Exception e) {
            log.error("운행 시작 처리 실패: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error("운행 시작 처리에 실패했습니다: " + e.getMessage()));
        }
    }
    
    /**
     * DTG 서비스에서 운행 종료 알림을 받음
     */
    @PostMapping("/trips/end")
    public ResponseEntity<ApiResponse<String>> handleTripEnded(@Valid @RequestBody TripEndRequest request) {
        log.info("DTG에서 운행 종료 알림 수신: 차량={}", request.getVehicleId());
        
        try {
            dtgIntegrationService.handleTripEnded(request);
            return ResponseEntity.ok(ApiResponse.success("운행 종료가 처리되었습니다."));
        } catch (Exception e) {
            log.error("운행 종료 처리 실패: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error("운행 종료 처리에 실패했습니다: " + e.getMessage()));
        }
    }
    
    /**
     * DTG 서비스에서 실시간 추적 데이터를 받음
     */
    @PostMapping("/data")
    public ResponseEntity<ApiResponse<String>> handleTrackingData(@Valid @RequestBody TrackingData data) {
        log.debug("DTG에서 추적 데이터 수신: 차량={}, 위치=({}, {})", 
                data.getVehicleId(), data.getLatitude(), data.getLongitude());
        
        try {
            dtgIntegrationService.handleTrackingData(data);
            return ResponseEntity.ok(ApiResponse.success("추적 데이터가 처리되었습니다."));
        } catch (Exception e) {
            log.error("추적 데이터 처리 실패: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error("추적 데이터 처리에 실패했습니다: " + e.getMessage()));
        }
    }
}
