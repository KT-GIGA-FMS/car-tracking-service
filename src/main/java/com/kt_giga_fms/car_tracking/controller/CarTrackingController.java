package com.kt_giga_fms.car_tracking.controller;

import com.kt_giga_fms.car_tracking.dto.CarTrackingRequest;
import com.kt_giga_fms.car_tracking.dto.CarTrackingResponse;
import com.kt_giga_fms.car_tracking.dto.ApiListResponse;
import com.kt_giga_fms.car_tracking.service.CarTrackingService;
import com.kt_giga_fms.car_tracking.exception.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Car Tracking", description = "차량 추적 및 위치 모니터링 API")
public class CarTrackingController {
    
    private final CarTrackingService carTrackingService;
    
    // DTG로부터 차량 위치 정보 수신 (POST)
    @PostMapping("/receive")
    @Operation(summary = "차량 위치 정보 수신", description = "DTG로부터 차량의 실시간 위치 정보를 수신합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "위치 정보 수신 성공", 
                    content = @Content(schema = @Schema(implementation = CarTrackingResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<CarTrackingResponse> receiveVehicleLocation(
            @Parameter(description = "차량 위치 정보") @RequestBody CarTrackingRequest request) {
        CarTrackingResponse response = carTrackingService.receiveVehicleLocation(request);
        return ResponseEntity.ok(response);
    }
    
    // 모든 차량의 현재 위치 정보 조회 (실시간 지도 표시용)
    @GetMapping("/current-locations")
    @Operation(summary = "전체 차량 현재 위치 조회", description = "모든 차량의 현재 위치 정보를 조회합니다. 실시간 지도 표시용입니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "위치 정보 조회 성공", 
                    content = @Content(schema = @Schema(implementation = ApiListResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<ApiListResponse<CarTrackingResponse>> getAllCurrentVehicleLocations() {
        List<CarTrackingResponse> locations = carTrackingService.getAllCurrentVehicleLocations();
        return ResponseEntity.ok(ApiListResponse.of(locations));
    }
    
    // 특정 차량의 최신 위치 정보 조회
    @GetMapping("/vehicle/{vehicleId}/latest")
    @Operation(summary = "차량별 최신 위치 조회", description = "특정 차량의 최신 위치 정보를 조회합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "위치 정보 조회 성공", 
                    content = @Content(schema = @Schema(implementation = CarTrackingResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "차량을 찾을 수 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<CarTrackingResponse> getLatestVehicleLocation(
            @Parameter(description = "차량 ID") @PathVariable String vehicleId) {
        Optional<CarTrackingResponse> location = carTrackingService.getLatestVehicleLocation(vehicleId);
        return ResponseEntity.ok(location.orElseThrow(() -> new NotFoundException("Vehicle not found: " + vehicleId)));
    }
    
    // 특정 차량의 위치 이력 조회
    @GetMapping("/vehicle/{vehicleId}/history")
    @Operation(summary = "차량 위치 이력 조회", description = "특정 차량의 위치 이력을 조회합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "위치 이력 조회 성공", 
                    content = @Content(schema = @Schema(implementation = ApiListResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "차량을 찾을 수 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<ApiListResponse<CarTrackingResponse>> getVehicleLocationHistory(
            @Parameter(description = "차량 ID") @PathVariable String vehicleId) {
        List<CarTrackingResponse> history = carTrackingService.getVehicleLocationHistory(vehicleId);
        return ResponseEntity.ok(ApiListResponse.of(history));
    }
    
    // 특정 시간 범위 내의 차량 위치 정보 조회
    @GetMapping("/locations/time-range")
    @Operation(summary = "시간 범위별 위치 조회", description = "특정 시간 범위 내의 모든 차량 위치 정보를 조회합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "위치 정보 조회 성공", 
                    content = @Content(schema = @Schema(implementation = ApiListResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 시간 범위"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<ApiListResponse<CarTrackingResponse>> getVehicleLocationsInTimeRange(
            @Parameter(description = "시작 시간 (ISO 형식)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "종료 시간 (ISO 형식)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        List<CarTrackingResponse> locations = carTrackingService.getVehicleLocationsInTimeRange(startTime, endTime);
        return ResponseEntity.ok(ApiListResponse.of(locations));
    }
    
    // 특정 지역 내의 차량들 조회
    @GetMapping("/locations/area")
    @Operation(summary = "지역별 차량 조회", description = "특정 지역 내에 있는 차량들을 조회합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "차량 조회 성공", 
                    content = @Content(schema = @Schema(implementation = ApiListResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 좌표 범위"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<ApiListResponse<CarTrackingResponse>> getVehiclesInArea(
            @Parameter(description = "최소 위도") @RequestParam Double minLat,
            @Parameter(description = "최대 위도") @RequestParam Double maxLat,
            @Parameter(description = "최소 경도") @RequestParam Double minLng,
            @Parameter(description = "최대 경도") @RequestParam Double maxLng) {
        List<CarTrackingResponse> vehicles = carTrackingService.getVehiclesInArea(minLat, maxLat, minLng, maxLng);
        return ResponseEntity.ok(ApiListResponse.of(vehicles));
    }
    
    // 특정 상태의 차량들 조회
    @GetMapping("/vehicles/status/{status}")
    @Operation(summary = "상태별 차량 조회", description = "특정 상태의 차량들을 조회합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "차량 조회 성공", 
                    content = @Content(schema = @Schema(implementation = ApiListResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 상태값"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<ApiListResponse<CarTrackingResponse>> getVehiclesByStatus(
            @Parameter(description = "차량 상태", example = "ACTIVE") @PathVariable String status) {
        List<CarTrackingResponse> vehicles = carTrackingService.getVehiclesByStatus(status);
        return ResponseEntity.ok(ApiListResponse.of(vehicles));
    }
    
    // 차량 상태 업데이트
    @PutMapping("/vehicle/{vehicleId}/status")
    @Operation(summary = "차량 상태 업데이트", description = "특정 차량의 상태를 업데이트합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상태 업데이트 성공", 
                    content = @Content(schema = @Schema(implementation = CarTrackingResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "차량을 찾을 수 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<CarTrackingResponse> updateVehicleStatus(
            @Parameter(description = "차량 ID") @PathVariable String vehicleId,
            @Parameter(description = "새로운 상태", example = "ACTIVE") @RequestParam String status) {
        CarTrackingResponse response = carTrackingService.updateVehicleStatus(vehicleId, status);
        return ResponseEntity.ok(response);
    }
    
    // 헬스체크 엔드포인트
    @GetMapping("/health")
    @Operation(summary = "헬스 체크", description = "서비스 상태를 확인합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "서비스 정상 동작")
    })
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Car Tracking Service is running");
    }
}
