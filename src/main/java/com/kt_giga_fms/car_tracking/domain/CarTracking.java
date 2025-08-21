package com.kt_giga_fms.car_tracking.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "car_tracking")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CarTracking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "vehicle_id", nullable = false)
    private String vehicleId;
    
    @Column(name = "vehicle_name")
    private String vehicleName;
    
    @Column(name = "latitude", nullable = false)
    private Double latitude;
    
    @Column(name = "longitude", nullable = false)
    private Double longitude;
    
    @Column(name = "speed")
    private Double speed;
    
    @Column(name = "heading")
    private Double heading;
    
    @Column(name = "status")
    private String status; // RUNNING, STOPPED, MAINTENANCE, etc.
    
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    
    @Column(name = "fuel_level")
    private Double fuelLevel;
    
    @Column(name = "engine_status")
    private String engineStatus; // ON, OFF

    // Domain update methods (no setters)
    public void updateStatus(String newStatus) {
        this.status = newStatus;
        this.timestamp = LocalDateTime.now();
    }
}
