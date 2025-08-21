package com.kt_giga_fms.car_tracking.service;

import com.kt_giga_fms.car_tracking.dto.CarTrackingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RealtimeTrackingService {

	private final SimpMessagingTemplate messagingTemplate;

	public void sendVehicleLocationUpdate(String vehicleId, CarTrackingResponse location) {
		messagingTemplate.convertAndSend("/topic/vehicle/" + vehicleId, location);
	}

	public void sendAllVehiclesUpdate(CarTrackingResponse location) {
		messagingTemplate.convertAndSend("/topic/vehicles/all", location);
	}
}
