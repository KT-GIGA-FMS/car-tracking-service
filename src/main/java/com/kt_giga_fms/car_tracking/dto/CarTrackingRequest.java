package com.kt_giga_fms.car_tracking.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import com.kt_giga_fms.car_tracking.domain.CarTracking;

@Getter
@Schema(description = "차량 위치 추적 요청 DTO")
public class CarTrackingRequest {
    
    @Schema(description = "차량 ID", example = "CAR001", required = true)
    private final String vehicleId;
    
    @Schema(description = "차량명", example = "현대 아반떼")
    private final String vehicleName;
    
    @Schema(description = "위도", example = "37.5665", required = true)
    private final Double latitude;
    
    @Schema(description = "경도", example = "126.9780", required = true)
    private final Double longitude;
    
    @Schema(description = "속도 (km/h)", example = "60.5")
    private final Double speed;
    
    @Schema(description = "방향 (도)", example = "180.0")
    private final Double heading;
    
    @Schema(description = "차량 상태", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE", "MAINTENANCE", "OFFLINE"})
    private final String status;
    
    @Schema(description = "타임스탬프", example = "2024-01-15T09:00:00")
    private final LocalDateTime timestamp;
    
    @Schema(description = "연료 레벨 (%)", example = "75.5")
    private final Double fuelLevel;
    
    @Schema(description = "엔진 상태", example = "RUNNING", allowableValues = {"RUNNING", "STOPPED", "IDLE"})
    private final String engineStatus;

    @JsonCreator
    public CarTrackingRequest(
            @JsonProperty("vehicleId") String vehicleId,
            @JsonProperty("vehicleName") String vehicleName,
            @JsonProperty("latitude") Double latitude,
            @JsonProperty("longitude") Double longitude,
            @JsonProperty("speed") Double speed,
            @JsonProperty("heading") Double heading,
            @JsonProperty("status") String status,
            @JsonProperty("timestamp") LocalDateTime timestamp,
            @JsonProperty("fuelLevel") Double fuelLevel,
            @JsonProperty("engineStatus") String engineStatus) {
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

    public CarTracking toCarTracking() {
        LocalDateTime eventTime = this.timestamp != null ? this.timestamp : LocalDateTime.now();
        return CarTracking.builder()
                .vehicleId(this.vehicleId)
                .vehicleName(this.vehicleName)
                .latitude(this.latitude)
                .longitude(this.longitude)
                .speed(this.speed)
                .heading(this.heading)
                .status(this.status)
                .timestamp(eventTime)
                .fuelLevel(this.fuelLevel)
                .engineStatus(this.engineStatus)
                .build();
    }
}
