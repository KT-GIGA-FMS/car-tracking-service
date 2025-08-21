package com.kt_giga_fms.car_tracking.dto;

import com.kt_giga_fms.car_tracking.domain.CarTracking;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CarTrackingResponse DTO 테스트")
class CarTrackingResponseTest {

    @Test
    @DisplayName("CarTrackingResponse 생성 - 성공")
    void createCarTrackingResponse_Success() {
        // given
        LocalDateTime timestamp = LocalDateTime.now();
        
        // when
        CarTrackingResponse response = new CarTrackingResponse(
                1L,
                "TEST001",
                "테스트 차량",
                37.5665,
                126.9780,
                60.0,
                90.0,
                "운행중",
                timestamp,
                80.0,
                "ON"
        );

        // then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getVehicleId()).isEqualTo("TEST001");
        assertThat(response.getVehicleName()).isEqualTo("테스트 차량");
        assertThat(response.getLatitude()).isEqualTo(37.5665);
        assertThat(response.getLongitude()).isEqualTo(126.9780);
        assertThat(response.getSpeed()).isEqualTo(60.0);
        assertThat(response.getHeading()).isEqualTo(90.0);
        assertThat(response.getStatus()).isEqualTo("운행중");
        assertThat(response.getTimestamp()).isEqualTo(timestamp);
        assertThat(response.getFuelLevel()).isEqualTo(80.0);
        assertThat(response.getEngineStatus()).isEqualTo("ON");
    }

    @Test
    @DisplayName("CarTrackingResponse.of() 정적 팩토리 메서드 - 성공")
    void of_StaticFactoryMethod_Success() {
        // given
        LocalDateTime timestamp = LocalDateTime.now();
        
        // when
        CarTrackingResponse response = CarTrackingResponse.of(
                1L,
                "TEST001",
                "테스트 차량",
                37.5665,
                126.9780,
                60.0,
                90.0,
                "운행중",
                timestamp,
                80.0,
                "ON"
        );

        // then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getVehicleId()).isEqualTo("TEST001");
        assertThat(response.getVehicleName()).isEqualTo("테스트 차량");
        assertThat(response.getLatitude()).isEqualTo(37.5665);
        assertThat(response.getLongitude()).isEqualTo(126.9780);
        assertThat(response.getSpeed()).isEqualTo(60.0);
        assertThat(response.getHeading()).isEqualTo(90.0);
        assertThat(response.getStatus()).isEqualTo("운행중");
        assertThat(response.getTimestamp()).isEqualTo(timestamp);
        assertThat(response.getFuelLevel()).isEqualTo(80.0);
        assertThat(response.getEngineStatus()).isEqualTo("ON");
    }

    @Test
    @DisplayName("CarTracking 엔티티로부터 CarTrackingResponse 생성 - 성공")
    void from_CarTrackingEntity_Success() {
        // given
        LocalDateTime timestamp = LocalDateTime.now();
        CarTracking carTracking = CarTracking.builder()
                .id(1L)
                .vehicleId("TEST001")
                .vehicleName("테스트 차량")
                .latitude(37.5665)
                .longitude(126.9780)
                .speed(60.0)
                .heading(90.0)
                .status("운행중")
                .timestamp(timestamp)
                .fuelLevel(80.0)
                .engineStatus("ON")
                .build();

        // when
        CarTrackingResponse response = CarTrackingResponse.from(carTracking);

        // then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getVehicleId()).isEqualTo("TEST001");
        assertThat(response.getVehicleName()).isEqualTo("테스트 차량");
        assertThat(response.getLatitude()).isEqualTo(37.5665);
        assertThat(response.getLongitude()).isEqualTo(126.9780);
        assertThat(response.getSpeed()).isEqualTo(60.0);
        assertThat(response.getHeading()).isEqualTo(90.0);
        assertThat(response.getStatus()).isEqualTo("운행중");
        assertThat(response.getTimestamp()).isEqualTo(timestamp);
        assertThat(response.getFuelLevel()).isEqualTo(80.0);
        assertThat(response.getEngineStatus()).isEqualTo("ON");
    }

    @Test
    @DisplayName("TrackingData로부터 CarTrackingResponse 생성 - 성공")
    void fromTrackingData_Success() {
        // given
        LocalDateTime timestamp = LocalDateTime.now();
        TrackingData trackingData = new TrackingData();
        trackingData.setVehicleId("TEST001");
        trackingData.setPlateNo("12가3456");
        trackingData.setLatitude(37.5665);
        trackingData.setLongitude(126.9780);
        trackingData.setSpeed(60.0);
        trackingData.setHeading(90.0);
        trackingData.setAltitude(50.0);
        trackingData.setFuelLevel(80.0);
        trackingData.setEngineStatus("ON");
        trackingData.setTimestamp(timestamp);
        trackingData.setTripId("TRIP001");

        // when
        CarTrackingResponse response = CarTrackingResponse.fromTrackingData(trackingData);

        // then
        assertThat(response.getId()).isNull();
        assertThat(response.getVehicleId()).isEqualTo("TEST001");
        assertThat(response.getVehicleName()).isEqualTo("12가3456"); // 번호판을 차량명으로 사용
        assertThat(response.getLatitude()).isEqualTo(37.5665);
        assertThat(response.getLongitude()).isEqualTo(126.9780);
        assertThat(response.getSpeed()).isEqualTo(60.0);
        assertThat(response.getHeading()).isEqualTo(90.0);
        assertThat(response.getStatus()).isEqualTo("운행중"); // 기본 상태
        assertThat(response.getTimestamp()).isEqualTo(timestamp);
        assertThat(response.getFuelLevel()).isEqualTo(80.0);
        assertThat(response.getEngineStatus()).isEqualTo("ON");
    }
}
