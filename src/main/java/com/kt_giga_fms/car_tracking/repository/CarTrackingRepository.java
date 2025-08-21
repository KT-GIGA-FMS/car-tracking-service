package com.kt_giga_fms.car_tracking.repository;

import com.kt_giga_fms.car_tracking.domain.CarTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CarTrackingRepository extends JpaRepository<CarTracking, Long> {
    
    // 특정 차량의 최신 위치 정보 조회
    Optional<CarTracking> findFirstByVehicleIdOrderByTimestampDesc(String vehicleId);
    
    // 특정 차량의 위치 이력 조회
    List<CarTracking> findByVehicleIdOrderByTimestampDesc(String vehicleId);
    
    // 특정 시간 범위 내의 모든 차량 위치 정보 조회
    List<CarTracking> findByTimestampBetweenOrderByTimestampDesc(LocalDateTime startTime, LocalDateTime endTime);
    
    // 특정 차량의 특정 시간 범위 내 위치 정보 조회
    List<CarTracking> findByVehicleIdAndTimestampBetweenOrderByTimestampDesc(
        String vehicleId, LocalDateTime startTime, LocalDateTime endTime);
    
    // 현재 운행 중인 모든 차량 조회 (최근 5분 내 데이터)
    @Query("SELECT c FROM CarTracking c WHERE c.timestamp >= :cutoffTime GROUP BY c.vehicleId HAVING c.timestamp = MAX(c.timestamp)")
    List<CarTracking> findCurrentActiveVehicles(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    // 특정 지역 내의 차량들 조회
    @Query("SELECT c FROM CarTracking c WHERE c.latitude BETWEEN :minLat AND :maxLat " +
           "AND c.longitude BETWEEN :minLng AND :maxLng " +
           "AND c.timestamp >= :cutoffTime " +
           "GROUP BY c.vehicleId HAVING c.timestamp = MAX(c.timestamp)")
    List<CarTracking> findVehiclesInArea(
        @Param("minLat") Double minLat, 
        @Param("maxLat") Double maxLat,
        @Param("minLng") Double minLng, 
        @Param("maxLng") Double maxLng,
        @Param("cutoffTime") LocalDateTime cutoffTime);
    
    // 특정 상태의 차량들 조회
    List<CarTracking> findByStatusAndTimestampAfterOrderByTimestampDesc(String status, LocalDateTime timestamp);
}
