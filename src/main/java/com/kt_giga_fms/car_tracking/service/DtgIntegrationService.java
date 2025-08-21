package com.kt_giga_fms.car_tracking.service;

import com.kt_giga_fms.car_tracking.dto.TripStartRequest;
import com.kt_giga_fms.car_tracking.dto.TripEndRequest;
import com.kt_giga_fms.car_tracking.dto.TrackingData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DtgIntegrationService {
    
    private final SimpMessagingTemplate messagingTemplate;
    private final CarTrackingService carTrackingService;
    
    /**
     * DTG에서 운행 시작 알림을 받아 처리
     */
    public void handleTripStarted(TripStartRequest request) {
        log.info("운행 시작 처리: 차량={}, 운전자={}", request.getVehicleId(), request.getDriverId());
        
        // Redis에 운행 시작 정보 저장
        carTrackingService.saveTripStartInfo(request);
        
        // 프론트엔드에 WebSocket으로 운행 시작 알림
        notifyFrontendTripStarted(request);
        
        // analytics-service에 운행 시작 알림 (필요시)
        notifyAnalyticsServiceTripStarted(request);
    }
    
    /**
     * DTG에서 운행 종료 알림을 받아 처리
     */
    public void handleTripEnded(TripEndRequest request) {
        log.info("운행 종료 처리: 차량={}", request.getVehicleId());
        
        // Redis에 운행 종료 정보 저장
        carTrackingService.saveTripEndInfo(request);
        
        // 프론트엔드에 WebSocket으로 운행 종료 알림
        notifyFrontendTripEnded(request);
        
        // analytics-service에 운행 완료 데이터 전송
        notifyAnalyticsServiceTripCompleted(request);
    }
    
    /**
     * DTG에서 실시간 추적 데이터를 받아 처리
     */
    public void handleTrackingData(TrackingData data) {
        // Redis에 실시간 추적 데이터 저장
        carTrackingService.saveTrackingData(data);
        
        // 프론트엔드에 WebSocket으로 실시간 데이터 전송
        sendTrackingDataToFrontend(data);
    }
    
    /**
     * 프론트엔드에 운행 시작 알림
     */
    private void notifyFrontendTripStarted(TripStartRequest request) {
        try {
            String destination = "/topic/trips/" + request.getVehicleId() + "/start";
            messagingTemplate.convertAndSend(destination, request);
            log.info("프론트엔드에 운행 시작 알림 전송: 차량={}", request.getVehicleId());
        } catch (Exception e) {
            log.error("프론트엔드 운행 시작 알림 실패: {}", e.getMessage());
        }
    }
    
    /**
     * 프론트엔드에 운행 종료 알림
     */
    private void notifyFrontendTripEnded(TripEndRequest request) {
        try {
            String destination = "/topic/trips/" + request.getVehicleId() + "/end";
            messagingTemplate.convertAndSend(destination, request);
            log.info("프론트엔드에 운행 종료 알림 전송: 차량={}", request.getVehicleId());
        } catch (Exception e) {
            log.error("프론트엔드 운행 종료 알림 실패: {}", e.getMessage());
        }
    }
    
    /**
     * 프론트엔드에 실시간 추적 데이터 전송
     */
    private void sendTrackingDataToFrontend(TrackingData data) {
        try {
            String destination = "/topic/tracking/" + data.getVehicleId();
            messagingTemplate.convertAndSend(destination, data);
        } catch (Exception e) {
            log.error("프론트엔드 추적 데이터 전송 실패: {}", e.getMessage());
        }
    }
    
    /**
     * analytics-service에 운행 시작 알림
     */
    private void notifyAnalyticsServiceTripStarted(TripStartRequest request) {
        try {
            // analytics-service에 운행 시작 알림
            log.info("analytics-service에 운행 시작 알림: 차량={}", request.getVehicleId());
        } catch (Exception e) {
            log.error("analytics-service 운행 시작 알림 실패: {}", e.getMessage());
        }
    }
    
    /**
     * analytics-service에 운행 완료 데이터 전송
     */
    private void notifyAnalyticsServiceTripCompleted(TripEndRequest request) {
        try {
            // analytics-service에 운행 완료 데이터 전송
            log.info("analytics-service에 운행 완료 데이터 전송: 차량={}", request.getVehicleId());
        } catch (Exception e) {
            log.error("analytics-service 운행 완료 데이터 전송 실패: {}", e.getMessage());
        }
    }
}
