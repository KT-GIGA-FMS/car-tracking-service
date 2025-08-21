package com.kt_giga_fms.car_tracking.dto;

import com.kt_giga_fms.car_tracking.domain.CarTracking;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CarTrackingRequest DTO 테스트")
class CarTrackingRequestTest {

    @Test
    @DisplayName("CarTrackingRequest 생성 - 성공")
    void createCarTrackingRequest_Success() {
        // given
        LocalDateTime timestamp = LocalDateTime.now();
        
        // when
        CarTrackingRequest request = new CarTrackingRequest(
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
        assertThat(request.getVehicleId()).isEqualTo("TEST001");
        assertThat(request.getVehicleName()).isEqualTo("테스트 차량");
        assertThat(request.getLatitude()).isEqualTo(37.5665);
        assertThat(request.getLongitude()).isEqualTo(126.9780);
        assertThat(request.getSpeed()).isEqualTo(60.0);
        assertThat(request.getHeading()).isEqualTo(90.0);
        assertThat(request.getStatus()).isEqualTo("운행중");
        assertThat(request.getTimestamp()).isEqualTo(timestamp);
        assertThat(request.getFuelLevel()).isEqualTo(80.0);
        assertThat(request.getEngineStatus()).isEqualTo("ON");
    }

    @Test
    @DisplayName("CarTrackingRequest를 CarTracking 엔티티로 변환 - timestamp가 있는 경우")
    void toCarTracking_WithTimestamp_Success() {
        // given
        LocalDateTime timestamp = LocalDateTime.of(2024, 1, 1, 12, 0, 0);
        CarTrackingRequest request = new CarTrackingRequest(
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

        // when
        CarTracking carTracking = request.toCarTracking();

        // then
        assertThat(carTracking.getVehicleId()).isEqualTo("TEST001");
        assertThat(carTracking.getVehicleName()).isEqualTo("테스트 차량");
        assertThat(carTracking.getLatitude()).isEqualTo(37.5665);
        assertThat(carTracking.getLongitude()).isEqualTo(126.9780);
        assertThat(carTracking.getSpeed()).isEqualTo(60.0);
        assertThat(carTracking.getHeading()).isEqualTo(90.0);
        assertThat(carTracking.getStatus()).isEqualTo("운행중");
        assertThat(carTracking.getTimestamp()).isEqualTo(timestamp);
        assertThat(carTracking.getFuelLevel()).isEqualTo(80.0);
        assertThat(carTracking.getEngineStatus()).isEqualTo("ON");
    }

    @Test
    @DisplayName("CarTrackingRequest를 CarTracking 엔티티로 변환 - timestamp가 null인 경우")
    void toCarTracking_WithNullTimestamp_Success() {
        // given
        CarTrackingRequest request = new CarTrackingRequest(
                "TEST001",
                "테스트 차량",
                37.5665,
                126.9780,
                60.0,
                90.0,
                "운행중",
                null,
                80.0,
                "ON"
        );

        // when
        CarTracking carTracking = request.toCarTracking();

        // then
        assertThat(carTracking.getVehicleId()).isEqualTo("TEST001");
        assertThat(carTracking.getVehicleName()).isEqualTo("테스트 차량");
        assertThat(carTracking.getLatitude()).isEqualTo(37.5665);
        assertThat(carTracking.getLongitude()).isEqualTo(126.9780);
        assertThat(carTracking.getSpeed()).isEqualTo(60.0);
        assertThat(carTracking.getHeading()).isEqualTo(90.0);
        assertThat(carTracking.getStatus()).isEqualTo("운행중");
        assertThat(carTracking.getTimestamp()).isNotNull();
        assertThat(carTracking.getFuelLevel()).isEqualTo(80.0);
        assertThat(carTracking.getEngineStatus()).isEqualTo("ON");
    }
}
