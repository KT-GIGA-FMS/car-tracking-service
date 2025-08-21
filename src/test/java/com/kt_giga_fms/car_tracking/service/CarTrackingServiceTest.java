package com.kt_giga_fms.car_tracking.service;

import com.kt_giga_fms.car_tracking.domain.CarTracking;
import com.kt_giga_fms.car_tracking.dto.CarTrackingRequest;
import com.kt_giga_fms.car_tracking.dto.CarTrackingResponse;
import com.kt_giga_fms.car_tracking.dto.TripStartRequest;
import com.kt_giga_fms.car_tracking.dto.TripEndRequest;
import com.kt_giga_fms.car_tracking.dto.TrackingData;
import com.kt_giga_fms.car_tracking.repository.CarTrackingRedisRepository;
import com.kt_giga_fms.car_tracking.repository.CarTrackingRepository;
import com.kt_giga_fms.car_tracking.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CarTrackingService 단위 테스트")
class CarTrackingServiceTest {

    @Mock
    private CarTrackingRepository carTrackingRepository;

    @Mock
    private CarTrackingRedisRepository carTrackingRedisRepository;

    @Mock
    private RealtimeTrackingService realtimeTrackingService;

    @InjectMocks
    private CarTrackingService carTrackingService;

    private CarTrackingRequest carTrackingRequest;
    private CarTrackingResponse carTrackingResponse;
    private CarTracking carTracking;

    @BeforeEach
    void setUp() {
        carTrackingRequest = new CarTrackingRequest(
                "TEST001",
                "테스트 차량",
                37.5665,
                126.9780,
                60.0,
                90.0,
                "운행중",
                LocalDateTime.now(),
                80.0,
                "ON"
        );

        carTracking = carTrackingRequest.toCarTracking();
        carTrackingResponse = CarTrackingResponse.from(carTracking);
    }

    @Test
    @DisplayName("차량 위치 정보 수신 및 저장 - 성공")
    void receiveVehicleLocation_Success() {
        // when
        CarTrackingResponse result = carTrackingService.receiveVehicleLocation(carTrackingRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getVehicleId()).isEqualTo("TEST001");
        assertThat(result.getVehicleName()).isEqualTo("테스트 차량");
        assertThat(result.getLatitude()).isEqualTo(37.5665);
        assertThat(result.getLongitude()).isEqualTo(126.9780);

        verify(carTrackingRedisRepository).saveLatest(any(CarTrackingResponse.class));
        verify(carTrackingRedisRepository).appendHistory(any(CarTrackingResponse.class));
        verify(realtimeTrackingService).sendVehicleLocationUpdate(eq("TEST001"), any(CarTrackingResponse.class));
        verify(realtimeTrackingService).sendAllVehiclesUpdate(any(CarTrackingResponse.class));
    }

    @Test
    @DisplayName("특정 차량의 최신 위치 정보 조회 - Redis에서 조회 성공")
    void getLatestVehicleLocation_FromRedis_Success() {
        // given
        when(carTrackingRedisRepository.findLatest("TEST001")).thenReturn(carTrackingResponse);

        // when
        Optional<CarTrackingResponse> result = carTrackingService.getLatestVehicleLocation("TEST001");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getVehicleId()).isEqualTo("TEST001");
        verify(carTrackingRepository, never()).findFirstByVehicleIdOrderByTimestampDesc(anyString());
    }

    @Test
    @DisplayName("특정 차량의 최신 위치 정보 조회 - DB에서 조회 성공")
    void getLatestVehicleLocation_FromDatabase_Success() {
        // given
        when(carTrackingRedisRepository.findLatest("TEST001")).thenReturn(null);
        when(carTrackingRepository.findFirstByVehicleIdOrderByTimestampDesc("TEST001"))
                .thenReturn(Optional.of(carTracking));

        // when
        Optional<CarTrackingResponse> result = carTrackingService.getLatestVehicleLocation("TEST001");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getVehicleId()).isEqualTo("TEST001");
        verify(carTrackingRepository).findFirstByVehicleIdOrderByTimestampDesc("TEST001");
    }

    @Test
    @DisplayName("모든 차량의 현재 위치 정보 조회 - Redis에서 조회 성공")
    void getAllCurrentVehicleLocations_FromRedis_Success() {
        // given
        List<CarTrackingResponse> expectedResponses = Arrays.asList(carTrackingResponse);
        when(carTrackingRedisRepository.findAllLatest()).thenReturn(expectedResponses);

        // when
        List<CarTrackingResponse> result = carTrackingService.getAllCurrentVehicleLocations();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getVehicleId()).isEqualTo("TEST001");
        verify(carTrackingRepository, never()).findCurrentActiveVehicles(any(LocalDateTime.class));
    }

    @Test
    @DisplayName("모든 차량의 현재 위치 정보 조회 - DB에서 조회 성공")
    void getAllCurrentVehicleLocations_FromDatabase_Success() {
        // given
        when(carTrackingRedisRepository.findAllLatest()).thenReturn(Arrays.asList());
        when(carTrackingRepository.findCurrentActiveVehicles(any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(carTracking));

        // when
        List<CarTrackingResponse> result = carTrackingService.getAllCurrentVehicleLocations();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getVehicleId()).isEqualTo("TEST001");
        verify(carTrackingRepository).findCurrentActiveVehicles(any(LocalDateTime.class));
    }

    @Test
    @DisplayName("특정 차량의 위치 이력 조회 - Redis에서 조회 성공")
    void getVehicleLocationHistory_FromRedis_Success() {
        // given
        List<CarTrackingResponse> expectedHistory = Arrays.asList(carTrackingResponse);
        when(carTrackingRedisRepository.findHistory("TEST001", 600)).thenReturn(expectedHistory);

        // when
        List<CarTrackingResponse> result = carTrackingService.getVehicleLocationHistory("TEST001");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getVehicleId()).isEqualTo("TEST001");
        verify(carTrackingRepository, never()).findByVehicleIdOrderByTimestampDesc(anyString());
    }

    @Test
    @DisplayName("특정 차량의 위치 이력 조회 - DB에서 조회 성공")
    void getVehicleLocationHistory_FromDatabase_Success() {
        // given
        when(carTrackingRedisRepository.findHistory("TEST001", 600)).thenReturn(Arrays.asList());
        when(carTrackingRepository.findByVehicleIdOrderByTimestampDesc("TEST001"))
                .thenReturn(Arrays.asList(carTracking));

        // when
        List<CarTrackingResponse> result = carTrackingService.getVehicleLocationHistory("TEST001");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getVehicleId()).isEqualTo("TEST001");
        verify(carTrackingRepository).findByVehicleIdOrderByTimestampDesc("TEST001");
    }

    @Test
    @DisplayName("특정 지역 내의 차량들 조회 - 성공")
    void getVehiclesInArea_Success() {
        // given
        List<CarTrackingResponse> allVehicles = Arrays.asList(carTrackingResponse);
        when(carTrackingRedisRepository.findAllLatest()).thenReturn(allVehicles);

        // when
        List<CarTrackingResponse> result = carTrackingService.getVehiclesInArea(37.0, 38.0, 126.0, 127.0);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getVehicleId()).isEqualTo("TEST001");
    }

    @Test
    @DisplayName("특정 지역 내의 차량들 조회 - 범위 밖 차량 제외")
    void getVehiclesInArea_OutOfRange_Excluded() {
        // given
        List<CarTrackingResponse> allVehicles = Arrays.asList(carTrackingResponse);
        when(carTrackingRedisRepository.findAllLatest()).thenReturn(allVehicles);

        // when
        List<CarTrackingResponse> result = carTrackingService.getVehiclesInArea(0.0, 1.0, 0.0, 1.0);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("특정 상태의 차량들 조회 - 성공")
    void getVehiclesByStatus_Success() {
        // given
        List<CarTrackingResponse> allVehicles = Arrays.asList(carTrackingResponse);
        when(carTrackingRedisRepository.findAllLatest()).thenReturn(allVehicles);

        // when
        List<CarTrackingResponse> result = carTrackingService.getVehiclesByStatus("운행중");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getVehicleId()).isEqualTo("TEST001");
        assertThat(result.get(0).getStatus()).isEqualTo("운행중");
    }

    @Test
    @DisplayName("운행 시작 정보 저장 - 성공")
    void saveTripStartInfo_Success() {
        // given
        TripStartRequest request = new TripStartRequest();
        request.setVehicleId("TEST001");
        request.setDriverId("DRIVER001");

        // when
        carTrackingService.saveTripStartInfo(request);

        // then
        verify(carTrackingRedisRepository).saveTripStartInfo(request);
    }

    @Test
    @DisplayName("운행 종료 정보 저장 - 성공")
    void saveTripEndInfo_Success() {
        // given
        TripEndRequest request = new TripEndRequest();
        request.setVehicleId("TEST001");

        // when
        carTrackingService.saveTripEndInfo(request);

        // then
        verify(carTrackingRedisRepository).saveTripEndInfo(request);
    }

    @Test
    @DisplayName("실시간 추적 데이터 저장 - 성공")
    void saveTrackingData_Success() {
        // given
        TrackingData data = new TrackingData();
        data.setVehicleId("TEST001");
        data.setLatitude(37.5665);
        data.setLongitude(126.9780);
        data.setSpeed(60.0);
        data.setTimestamp(LocalDateTime.now());

        // when
        carTrackingService.saveTrackingData(data);

        // then
        verify(carTrackingRedisRepository).saveTrackingData(data);
        verify(realtimeTrackingService).sendVehicleLocationUpdate(eq("TEST001"), any(CarTrackingResponse.class));
    }

    @Test
    @DisplayName("차량 상태 업데이트 - 성공")
    void updateVehicleStatus_Success() {
        // given
        when(carTrackingRedisRepository.findLatest("TEST001")).thenReturn(carTrackingResponse);

        // when
        CarTrackingResponse result = carTrackingService.updateVehicleStatus("TEST001", "정지");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("정지");
        verify(carTrackingRedisRepository).saveLatest(any(CarTrackingResponse.class));
        verify(carTrackingRedisRepository).appendHistory(any(CarTrackingResponse.class));
        verify(realtimeTrackingService).sendVehicleLocationUpdate(eq("TEST001"), any(CarTrackingResponse.class));
        verify(realtimeTrackingService).sendAllVehiclesUpdate(any(CarTrackingResponse.class));
    }

    @Test
    @DisplayName("차량 상태 업데이트 - 차량을 찾을 수 없음")
    void updateVehicleStatus_VehicleNotFound() {
        // given
        when(carTrackingRedisRepository.findLatest("INVALID")).thenReturn(null);
        when(carTrackingRepository.findFirstByVehicleIdOrderByTimestampDesc("INVALID"))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> carTrackingService.updateVehicleStatus("INVALID", "정지"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Vehicle not found: INVALID");
    }
}
