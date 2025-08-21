package com.kt_giga_fms.car_tracking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kt_giga_fms.car_tracking.dto.CarTrackingRequest;
import com.kt_giga_fms.car_tracking.dto.CarTrackingResponse;
import com.kt_giga_fms.car_tracking.dto.ApiListResponse;
import com.kt_giga_fms.car_tracking.service.CarTrackingService;
import com.kt_giga_fms.car_tracking.exception.NotFoundException;
import com.kt_giga_fms.car_tracking.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CarTrackingController 단위 테스트")
class CarTrackingControllerTest {

    @Mock
    private CarTrackingService carTrackingService;

    @InjectMocks
    private CarTrackingController carTrackingController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private CarTrackingRequest carTrackingRequest;
    private CarTrackingResponse carTrackingResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(carTrackingController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // LocalDateTime 직렬화를 위해 필요

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

        carTrackingResponse = new CarTrackingResponse(
                1L,
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
    }

    @Test
    @DisplayName("차량 위치 정보 수신 - 성공")
    void receiveVehicleLocation_Success() throws Exception {
        // given
        when(carTrackingService.receiveVehicleLocation(any(CarTrackingRequest.class)))
                .thenReturn(carTrackingResponse);

        // when & then
        mockMvc.perform(post("/api/v1/car-tracking/receive")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carTrackingRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehicleId").value("TEST001"))
                .andExpect(jsonPath("$.vehicleName").value("테스트 차량"))
                .andExpect(jsonPath("$.latitude").value(37.5665))
                .andExpect(jsonPath("$.longitude").value(126.9780));

        verify(carTrackingService).receiveVehicleLocation(any(CarTrackingRequest.class));
    }

    @Test
    @DisplayName("모든 차량의 현재 위치 정보 조회 - 성공")
    void getAllCurrentVehicleLocations_Success() throws Exception {
        // given
        List<CarTrackingResponse> locations = Arrays.asList(carTrackingResponse);
        when(carTrackingService.getAllCurrentVehicleLocations()).thenReturn(locations);

        // when & then
        mockMvc.perform(get("/api/v1/car-tracking/current-locations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[0].vehicleId").value("TEST001"))
                .andExpect(jsonPath("$.items[0].vehicleName").value("테스트 차량"));

        verify(carTrackingService).getAllCurrentVehicleLocations();
    }

    @Test
    @DisplayName("특정 차량의 최신 위치 정보 조회 - 성공")
    void getLatestVehicleLocation_Success() throws Exception {
        // given
        when(carTrackingService.getLatestVehicleLocation("TEST001"))
                .thenReturn(Optional.of(carTrackingResponse));

        // when & then
        mockMvc.perform(get("/api/v1/car-tracking/vehicle/TEST001/latest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehicleId").value("TEST001"))
                .andExpect(jsonPath("$.vehicleName").value("테스트 차량"));

        verify(carTrackingService).getLatestVehicleLocation("TEST001");
    }

    @Test
    @DisplayName("특정 차량의 최신 위치 정보 조회 - 차량을 찾을 수 없음")
    void getLatestVehicleLocation_VehicleNotFound() throws Exception {
        // given
        when(carTrackingService.getLatestVehicleLocation("INVALID"))
                .thenReturn(Optional.empty());

        // when & then
        mockMvc.perform(get("/api/v1/car-tracking/vehicle/INVALID/latest"))
                .andExpect(status().isNotFound());

        verify(carTrackingService).getLatestVehicleLocation("INVALID");
    }

    @Test
    @DisplayName("특정 차량의 위치 이력 조회 - 성공")
    void getVehicleLocationHistory_Success() throws Exception {
        // given
        List<CarTrackingResponse> history = Arrays.asList(carTrackingResponse);
        when(carTrackingService.getVehicleLocationHistory("TEST001")).thenReturn(history);

        // when & then
        mockMvc.perform(get("/api/v1/car-tracking/vehicle/TEST001/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[0].vehicleId").value("TEST001"));

        verify(carTrackingService).getVehicleLocationHistory("TEST001");
    }

    @Test
    @DisplayName("특정 시간 범위 내의 차량 위치 정보 조회 - 성공")
    void getVehicleLocationsInTimeRange_Success() throws Exception {
        // given
        LocalDateTime startTime = LocalDateTime.now().minusHours(1);
        LocalDateTime endTime = LocalDateTime.now();
        List<CarTrackingResponse> locations = Arrays.asList(carTrackingResponse);
        when(carTrackingService.getVehicleLocationsInTimeRange(startTime, endTime)).thenReturn(locations);

        // when & then
        mockMvc.perform(get("/api/v1/car-tracking/locations/time-range")
                        .param("startTime", startTime.toString())
                        .param("endTime", endTime.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[0].vehicleId").value("TEST001"));

        verify(carTrackingService).getVehicleLocationsInTimeRange(startTime, endTime);
    }

    @Test
    @DisplayName("특정 지역 내의 차량들 조회 - 성공")
    void getVehiclesInArea_Success() throws Exception {
        // given
        List<CarTrackingResponse> vehicles = Arrays.asList(carTrackingResponse);
        when(carTrackingService.getVehiclesInArea(37.0, 38.0, 126.0, 127.0)).thenReturn(vehicles);

        // when & then
        mockMvc.perform(get("/api/v1/car-tracking/locations/area")
                        .param("minLat", "37.0")
                        .param("maxLat", "38.0")
                        .param("minLng", "126.0")
                        .param("maxLng", "127.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[0].vehicleId").value("TEST001"));

        verify(carTrackingService).getVehiclesInArea(37.0, 38.0, 126.0, 127.0);
    }

    @Test
    @DisplayName("특정 상태의 차량들 조회 - 성공")
    void getVehiclesByStatus_Success() throws Exception {
        // given
        List<CarTrackingResponse> vehicles = Arrays.asList(carTrackingResponse);
        when(carTrackingService.getVehiclesByStatus("운행중")).thenReturn(vehicles);

        // when & then
        mockMvc.perform(get("/api/v1/car-tracking/vehicles/status/운행중"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[0].vehicleId").value("TEST001"))
                .andExpect(jsonPath("$.items[0].status").value("운행중"));

        verify(carTrackingService).getVehiclesByStatus("운행중");
    }

    @Test
    @DisplayName("차량 상태 업데이트 - 성공")
    void updateVehicleStatus_Success() throws Exception {
        // given
        CarTrackingResponse updatedResponse = new CarTrackingResponse(
                1L,
                "TEST001",
                "테스트 차량",
                37.5665,
                126.9780,
                60.0,
                90.0,
                "정지",
                LocalDateTime.now(),
                80.0,
                "ON"
        );

        when(carTrackingService.updateVehicleStatus("TEST001", "정지")).thenReturn(updatedResponse);

        // when & then
        mockMvc.perform(put("/api/v1/car-tracking/vehicle/TEST001/status")
                        .param("status", "정지"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehicleId").value("TEST001"))
                .andExpect(jsonPath("$.status").value("정지"));

        verify(carTrackingService).updateVehicleStatus("TEST001", "정지");
    }

    @Test
    @DisplayName("차량 상태 업데이트 - 차량을 찾을 수 없음")
    void updateVehicleStatus_VehicleNotFound() throws Exception {
        // given
        when(carTrackingService.updateVehicleStatus("INVALID", "정지"))
                .thenThrow(new NotFoundException("Vehicle not found: INVALID"));

        // when & then
        mockMvc.perform(put("/api/v1/car-tracking/vehicle/INVALID/status")
                        .param("status", "정지"))
                .andExpect(status().isNotFound());

        verify(carTrackingService).updateVehicleStatus("INVALID", "정지");
    }

    @Test
    @DisplayName("헬스체크 엔드포인트 - 성공")
    void healthCheck_Success() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/car-tracking/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Car Tracking Service is running"));
    }
}
