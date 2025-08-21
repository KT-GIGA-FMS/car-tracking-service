package com.kt_giga_fms.car_tracking.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Getter;
import com.kt_giga_fms.car_tracking.domain.CarTracking;

@Getter
public class CarTrackingRequest {
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
