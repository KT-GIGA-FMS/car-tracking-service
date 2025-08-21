package com.kt_giga_fms.car_tracking.repository;

import com.kt_giga_fms.car_tracking.dto.CarTrackingResponse;
import com.kt_giga_fms.car_tracking.dto.TripStartRequest;
import com.kt_giga_fms.car_tracking.dto.TripEndRequest;
import com.kt_giga_fms.car_tracking.dto.TrackingData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CarTrackingRedisRepository {

	private static final String VEHICLE_IDS_KEY = "vehicles:ids"; // Set<String>
	private static final String LATEST_KEY_PREFIX = "vehicle:latest:"; // String -> CarTrackingResponse
	private static final String HISTORY_KEY_PREFIX = "vehicle:history:"; // List -> CarTrackingResponse
	private static final String TRIP_START_KEY_PREFIX = "trip:start:"; // String -> TripStartRequest
	private static final String TRIP_END_KEY_PREFIX = "trip:end:"; // String -> TripEndRequest
	private static final int MAX_HISTORY_SIZE = 600; // ~10분 @1s

	private final RedisTemplate<String, CarTrackingResponse> redisTemplate;
	private final StringRedisTemplate stringRedisTemplate;
	private final ObjectMapper objectMapper;

	public void saveLatest(CarTrackingResponse response) {
		String vehicleId = response.getVehicleId();
		redisTemplate.opsForValue().set(latestKey(vehicleId), response);
		stringRedisTemplate.opsForSet().add(VEHICLE_IDS_KEY, vehicleId);
	}

	public void appendHistory(CarTrackingResponse response) {
		String key = historyKey(response.getVehicleId());
		redisTemplate.opsForList().leftPush(key, response);
		redisTemplate.opsForList().trim(key, 0, MAX_HISTORY_SIZE - 1);
	}

	public CarTrackingResponse findLatest(String vehicleId) {
		return redisTemplate.opsForValue().get(latestKey(vehicleId));
	}

	public List<CarTrackingResponse> findAllLatest() {
		Set<String> vehicleIds = stringRedisTemplate.opsForSet().members(VEHICLE_IDS_KEY);
		if (vehicleIds == null || vehicleIds.isEmpty()) return Collections.emptyList();
		List<CarTrackingResponse> results = new ArrayList<>(vehicleIds.size());
		for (String vehicleId : vehicleIds) {
			CarTrackingResponse r = findLatest(vehicleId);
			if (r != null) results.add(r);
		}
		return results;
	}

	public List<CarTrackingResponse> findHistory(String vehicleId, int limit) {
		List<CarTrackingResponse> list = redisTemplate.opsForList().range(historyKey(vehicleId), 0, limit - 1);
		return list != null ? list : Collections.emptyList();
	}

	public List<CarTrackingResponse> findHistoryInRange(String vehicleId, LocalDateTime start, LocalDateTime end) {
		List<CarTrackingResponse> list = redisTemplate.opsForList().range(historyKey(vehicleId), 0, MAX_HISTORY_SIZE - 1);
		if (list == null) return Collections.emptyList();
		return list.stream()
				.filter(r -> r.getTimestamp() != null && !r.getTimestamp().isBefore(start) && !r.getTimestamp().isAfter(end))
				.collect(Collectors.toList());
	}

	public List<String> findAllVehicleIds() {
		Set<String> vehicleIds = stringRedisTemplate.opsForSet().members(VEHICLE_IDS_KEY);
		if (vehicleIds == null) return Collections.emptyList();
		return new ArrayList<>(vehicleIds);
	}

	private String latestKey(String vehicleId) {
		return LATEST_KEY_PREFIX + vehicleId;
	}

	private String historyKey(String vehicleId) {
		return HISTORY_KEY_PREFIX + vehicleId;
	}
	
	// DTG 연동: 운행 시작 정보 저장
	public void saveTripStartInfo(TripStartRequest request) {
		try {
			String key = tripStartKey(request.getVehicleId());
			String json = objectMapper.writeValueAsString(request);
			stringRedisTemplate.opsForValue().set(key, json);
		} catch (JsonProcessingException e) {
			// 로깅 처리
		}
	}
	
	// DTG 연동: 운행 종료 정보 저장
	public void saveTripEndInfo(TripEndRequest request) {
		try {
			String key = tripEndKey(request.getVehicleId());
			String json = objectMapper.writeValueAsString(request);
			stringRedisTemplate.opsForValue().set(key, json);
		} catch (JsonProcessingException e) {
			// 로깅 처리
		}
	}
	
	// DTG 연동: 실시간 추적 데이터 저장
	public void saveTrackingData(TrackingData data) {
		// 추적 데이터를 CarTrackingResponse로 변환하여 저장
		CarTrackingResponse response = CarTrackingResponse.fromTrackingData(data);
		saveLatest(response);
		appendHistory(response);
	}
	
	private String tripStartKey(String vehicleId) {
		return TRIP_START_KEY_PREFIX + vehicleId;
	}
	
	private String tripEndKey(String vehicleId) {
		return TRIP_END_KEY_PREFIX + vehicleId;
	}
}
