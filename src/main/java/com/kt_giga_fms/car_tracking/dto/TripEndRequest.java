package com.kt_giga_fms.car_tracking.dto;

import lombok.Data;

@Data
public class TripEndRequest {
    
    private String vehicleId;
    private Double endLatitude;
    private Double endLongitude;
    private String endReason;
    private String notes;
}
