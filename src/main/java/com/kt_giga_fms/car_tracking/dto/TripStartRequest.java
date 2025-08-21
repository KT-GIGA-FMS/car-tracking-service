package com.kt_giga_fms.car_tracking.dto;

import lombok.Data;

@Data
public class TripStartRequest {
    
    private String vehicleId;
    private String plateNo;
    private String driverId;
    private Double startLatitude;
    private Double startLongitude;
    private String destination;
    private String purpose;
}
