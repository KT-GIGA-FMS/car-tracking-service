package com.kt_giga_fms.car_tracking.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import com.kt_giga_fms.car_tracking.domain.CarTracking;
import com.kt_giga_fms.car_tracking.dto.TrackingData;

@Getter
public class CarTrackingResponse {
    private final Long id;
    private final String vehicleId;
    private final String vehicleName;
    private final Double latitude;
    private final Double longitude;
    private final Double speed;
    private final Double heading;
    private final String status;
    private final LocalDateTime timestamp;
    private final Double fuelLevel;
    private final String engineStatus;

    public CarTrackingResponse(Long id, String vehicleId, String vehicleName, Double latitude, Double longitude,
                               Double speed, Double heading, String status, LocalDateTime timestamp,
                               Double fuelLevel, String engineStatus) {
        this.id = id;
        this.vehicleId = vehicleId;
        this.vehicleName = vehicleName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.heading = heading;
        this.status = status;
        this.timestamp = timestamp;
        this.fuelLevel = fuelLevel;
        this.engineStatus = engineStatus;
    }

    public static CarTrackingResponse of(Long id, String vehicleId, String vehicleName, Double latitude, Double longitude,
                                         Double speed, Double heading, String status, LocalDateTime timestamp,
                                         Double fuelLevel, String engineStatus) {
        return new CarTrackingResponse(id, vehicleId, vehicleName, latitude, longitude, speed, heading, status, timestamp, fuelLevel, engineStatus);
    }

    public static CarTrackingResponse from(CarTracking entity) {
        return of(
            entity.getId(),
            entity.getVehicleId(),
            entity.getVehicleName(),
            entity.getLatitude(),
            entity.getLongitude(),
            entity.getSpeed(),
            entity.getHeading(),
            entity.getStatus(),
            entity.getTimestamp(),
            entity.getFuelLevel(),
            entity.getEngineStatus()
        );
    }
    
    public static CarTrackingResponse fromTrackingData(TrackingData data) {
        return of(
            null, // ID는 없음
            data.getVehicleId(),
            data.getPlateNo(), // 번호판을 차량명으로 사용
            data.getLatitude(),
            data.getLongitude(),
            data.getSpeed(),
            data.getHeading(),
            "운행중", // 상태
            data.getTimestamp(),
            data.getFuelLevel(),
            data.getEngineStatus()
        );
    }
}
