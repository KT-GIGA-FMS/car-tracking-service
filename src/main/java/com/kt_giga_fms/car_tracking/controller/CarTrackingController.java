package com.kt_giga_fms.car_tracking.controller;

import com.kt_giga_fms.car_tracking.dto.CarTrackingRequest;
import com.kt_giga_fms.car_tracking.dto.CarTrackingResponse;
import com.kt_giga_fms.car_tracking.dto.ApiListResponse;
import com.kt_giga_fms.car_tracking.service.CarTrackingService;
import com.kt_giga_fms.car_tracking.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/car-tracking")
@RequiredArgsConstructor
public class CarTrackingController {
    
    private final CarTrackingService carTrackingService;
    
    // DTG로부터 차량 위치 정보 수신 (POST)
    @PostMapping("/receive")
    public ResponseEntity<CarTrackingResponse> receiveVehicleLocation(@RequestBody CarTrackingRequest request) {
        CarTrackingResponse response = carTrackingService.receiveVehicleLocation(request);
        return ResponseEntity.ok(response);
    }
    
    // 모든 차량의 현재 위치 정보 조회 (실시간 지도 표시용)
    @GetMapping("/current-locations")
    public ResponseEntity<ApiListResponse<CarTrackingResponse>> getAllCurrentVehicleLocations() {
        List<CarTrackingResponse> locations = carTrackingService.getAllCurrentVehicleLocations();
        return ResponseEntity.ok(ApiListResponse.of(locations));
    }
    
    // 특정 차량의 최신 위치 정보 조회
    @GetMapping("/vehicle/{vehicleId}/latest")
    public ResponseEntity<CarTrackingResponse> getLatestVehicleLocation(@PathVariable String vehicleId) {
        Optional<CarTrackingResponse> location = carTrackingService.getLatestVehicleLocation(vehicleId);
        return ResponseEntity.ok(location.orElseThrow(() -> new NotFoundException("Vehicle not found: " + vehicleId)));
    }
    
    // 특정 차량의 위치 이력 조회
    @GetMapping("/vehicle/{vehicleId}/history")
    public ResponseEntity<ApiListResponse<CarTrackingResponse>> getVehicleLocationHistory(@PathVariable String vehicleId) {
        List<CarTrackingResponse> history = carTrackingService.getVehicleLocationHistory(vehicleId);
        return ResponseEntity.ok(ApiListResponse.of(history));
    }
    
    // 특정 시간 범위 내의 차량 위치 정보 조회
    @GetMapping("/locations/time-range")
    public ResponseEntity<ApiListResponse<CarTrackingResponse>> getVehicleLocationsInTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        List<CarTrackingResponse> locations = carTrackingService.getVehicleLocationsInTimeRange(startTime, endTime);
        return ResponseEntity.ok(ApiListResponse.of(locations));
    }
    
    // 특정 지역 내의 차량들 조회
    @GetMapping("/locations/area")
    public ResponseEntity<ApiListResponse<CarTrackingResponse>> getVehiclesInArea(
            @RequestParam Double minLat,
            @RequestParam Double maxLat,
            @RequestParam Double minLng,
            @RequestParam Double maxLng) {
        List<CarTrackingResponse> vehicles = carTrackingService.getVehiclesInArea(minLat, maxLat, minLng, maxLng);
        return ResponseEntity.ok(ApiListResponse.of(vehicles));
    }
    
    // 특정 상태의 차량들 조회
    @GetMapping("/vehicles/status/{status}")
    public ResponseEntity<ApiListResponse<CarTrackingResponse>> getVehiclesByStatus(@PathVariable String status) {
        List<CarTrackingResponse> vehicles = carTrackingService.getVehiclesByStatus(status);
        return ResponseEntity.ok(ApiListResponse.of(vehicles));
    }
    
    // 차량 상태 업데이트
    @PutMapping("/vehicle/{vehicleId}/status")
    public ResponseEntity<CarTrackingResponse> updateVehicleStatus(
            @PathVariable String vehicleId,
            @RequestParam String status) {
        CarTrackingResponse response = carTrackingService.updateVehicleStatus(vehicleId, status);
        return ResponseEntity.ok(response);
    }
    
    // 헬스체크 엔드포인트
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Car Tracking Service is running");
    }
}
