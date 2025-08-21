package com.kt_giga_fms.car_tracking.service;

import com.kt_giga_fms.car_tracking.domain.CarTracking;
import com.kt_giga_fms.car_tracking.dto.CarTrackingRequest;
import com.kt_giga_fms.car_tracking.dto.CarTrackingResponse;
import com.kt_giga_fms.car_tracking.dto.TripStartRequest;
import com.kt_giga_fms.car_tracking.dto.TripEndRequest;
import com.kt_giga_fms.car_tracking.dto.TrackingData;
import com.kt_giga_fms.car_tracking.repository.CarTrackingRedisRepository;
import com.kt_giga_fms.car_tracking.repository.CarTrackingRepository;
import com.kt_giga_fms.car_tracking.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CarTrackingService {
    
    private final CarTrackingRepository carTrackingRepository;
    private final CarTrackingRedisRepository carTrackingRedisRepository;
    private final RealtimeTrackingService realtimeTrackingService;
    
    // DTG로부터 차량 위치 정보 수신 및 저장 (실시간은 Redis 우선)
    public CarTrackingResponse receiveVehicleLocation(CarTrackingRequest request) {
        CarTracking entity = request.toCarTracking();
        // Redis 최신/히스토리 업데이트
        CarTrackingResponse response = CarTrackingResponse.from(entity);
        carTrackingRedisRepository.saveLatest(response);
        carTrackingRedisRepository.appendHistory(response);
        // WebSocket 브로드캐스트
        realtimeTrackingService.sendVehicleLocationUpdate(response.getVehicleId(), response);
        realtimeTrackingService.sendAllVehiclesUpdate(response);
        return response;
    }
    
    // 특정 차량의 최신 위치 정보 조회 (Redis 우선, 실패 시 DB)
    @Transactional(readOnly = true)
    public Optional<CarTrackingResponse> getLatestVehicleLocation(String vehicleId) {
        CarTrackingResponse cached = carTrackingRedisRepository.findLatest(vehicleId);
        if (cached != null) return Optional.of(cached);
        return carTrackingRepository.findFirstByVehicleIdOrderByTimestampDesc(vehicleId)
                .map(CarTrackingResponse::from);
    }
    
    // 모든 차량의 현재 위치 정보 조회 (Redis set에 등록된 차량 아이디 기반)
    @Transactional(readOnly = true)
    public List<CarTrackingResponse> getAllCurrentVehicleLocations() {
        List<CarTrackingResponse> cached = carTrackingRedisRepository.findAllLatest();
        if (!cached.isEmpty()) return cached;
        // 캐시에 없으면 DB에서 최근 5분 내 최신 위치 추출
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(5);
        return carTrackingRepository.findCurrentActiveVehicles(cutoffTime)
                .stream().map(CarTrackingResponse::from).collect(Collectors.toList());
    }
    
    // 특정 차량의 위치 이력 조회 (Redis 히스토리 사용; 부족하면 DB로 확장 가능)
    @Transactional(readOnly = true)
    public List<CarTrackingResponse> getVehicleLocationHistory(String vehicleId) {
        List<CarTrackingResponse> history = carTrackingRedisRepository.findHistory(vehicleId, 600);
        if (!history.isEmpty()) return history;
        return carTrackingRepository.findByVehicleIdOrderByTimestampDesc(vehicleId)
                .stream().map(CarTrackingResponse::from).collect(Collectors.toList());
    }
    
    // 특정 시간 범위 내의 차량 위치 정보 조회 (주로 분석: DB 사용)
    @Transactional(readOnly = true)
    public List<CarTrackingResponse> getVehicleLocationsInTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return carTrackingRedisRepository.findAllVehicleIds().stream()
                .flatMap(id -> carTrackingRedisRepository.findHistoryInRange(id, startTime, endTime).stream())
                .collect(Collectors.toList());
    }
    
    // 특정 지역 내의 차량들 조회 (단순히 최신 좌표만 필터링)
    @Transactional(readOnly = true)
    public List<CarTrackingResponse> getVehiclesInArea(Double minLat, Double maxLat, Double minLng, Double maxLng) {
        List<CarTrackingResponse> latest = getAllCurrentVehicleLocations();
        List<CarTrackingResponse> filtered = new ArrayList<>();
        for (CarTrackingResponse r : latest) {
            if (r.getLatitude() == null || r.getLongitude() == null) continue;
            if (r.getLatitude() >= minLat && r.getLatitude() <= maxLat &&
                r.getLongitude() >= minLng && r.getLongitude() <= maxLng) {
                filtered.add(r);
            }
        }
        return filtered;
    }
    
    // 특정 상태의 차량들 조회 (최신 상태 기준)
    @Transactional(readOnly = true)
    public List<CarTrackingResponse> getVehiclesByStatus(String status) {
        return getAllCurrentVehicleLocations().stream()
                .filter(r -> status.equals(r.getStatus()))
                .collect(Collectors.toList());
    }
    
    // DTG 연동: 운행 시작 정보 저장
    public void saveTripStartInfo(TripStartRequest request) {
        // Redis에 운행 시작 정보 저장
        carTrackingRedisRepository.saveTripStartInfo(request);
        log.info("운행 시작 정보 저장 완료: 차량={}", request.getVehicleId());
    }
    
    // DTG 연동: 운행 종료 정보 저장
    public void saveTripEndInfo(TripEndRequest request) {
        // Redis에 운행 종료 정보 저장
        carTrackingRedisRepository.saveTripEndInfo(request);
        log.info("운행 종료 정보 저장 완료: 차량={}", request.getVehicleId());
    }
    
    // DTG 연동: 실시간 추적 데이터 저장
    public void saveTrackingData(TrackingData data) {
        // Redis에 실시간 추적 데이터 저장
        carTrackingRedisRepository.saveTrackingData(data);
        
        // WebSocket으로 프론트엔드에 실시간 데이터 전송
        realtimeTrackingService.sendVehicleLocationUpdate(data.getVehicleId(), 
            CarTrackingResponse.fromTrackingData(data));
    }
    
    // 차량 상태 업데이트 (최근 엔트리 기준으로 상태만 업데이트)
    public CarTrackingResponse updateVehicleStatus(String vehicleId, String status) {
        Optional<CarTrackingResponse> latest = getLatestVehicleLocation(vehicleId);
        if (latest.isPresent()) {
            CarTrackingResponse current = latest.get();
            CarTrackingResponse updated = CarTrackingResponse.of(
                    current.getId(),
                    current.getVehicleId(),
                    current.getVehicleName(),
                    current.getLatitude(),
                    current.getLongitude(),
                    current.getSpeed(),
                    current.getHeading(),
                    status,
                    LocalDateTime.now(),
                    current.getFuelLevel(),
                    current.getEngineStatus()
            );
            carTrackingRedisRepository.saveLatest(updated);
            carTrackingRedisRepository.appendHistory(updated);
            // WebSocket 브로드캐스트
            realtimeTrackingService.sendVehicleLocationUpdate(updated.getVehicleId(), updated);
            realtimeTrackingService.sendAllVehiclesUpdate(updated);
            return updated;
        }
        throw new NotFoundException("Vehicle not found: " + vehicleId);
    }
}
