CREATE TABLE car_tracking (
    id BIGSERIAL PRIMARY KEY,
    vehicle_id VARCHAR(100) NOT NULL,
    vehicle_name VARCHAR(200),
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    speed DOUBLE PRECISION,
    heading DOUBLE PRECISION,
    status VARCHAR(50),
    timestamp TIMESTAMP NOT NULL,
    fuel_level DOUBLE PRECISION,
    engine_status VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 인덱스 생성
CREATE INDEX idx_car_tracking_vehicle_id ON car_tracking(vehicle_id);
CREATE INDEX idx_car_tracking_timestamp ON car_tracking(timestamp);
CREATE INDEX idx_car_tracking_location ON car_tracking(latitude, longitude);
CREATE INDEX idx_car_tracking_status ON car_tracking(status);

-- 차량 ID와 타임스탬프의 복합 인덱스 (최신 위치 조회 최적화)
CREATE INDEX idx_car_tracking_vehicle_timestamp ON car_tracking(vehicle_id, timestamp DESC);
