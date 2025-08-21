# Car Tracking Service

차량 운행 기록을 DTG로부터 받아서 실시간으로 지도에 표시하는 백엔드 서비스입니다.

## 주요 기능

- DTG로부터 차량 위치 정보 수신 및 저장
- 실시간 차량 위치 정보 조회
- 차량 운행 이력 조회
- 지역별 차량 검색
- 차량 상태 관리

## 기술 스택

- **Backend**: Spring Boot 3.5.4, Java 17
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA
- **Migration**: Flyway
- **Cache**: Redis
- **Build Tool**: Gradle

## 프로젝트 구조

```
src/main/java/com/kt_giga_fms/car_tracking/
├── CarTrackingApplication.java          # 메인 애플리케이션 클래스
├── controller/
│   └── CarTrackingController.java      # REST API 컨트롤러
├── service/
│   └── CarTrackingService.java         # 비즈니스 로직 서비스
├── repository/
│   └── CarTrackingRepository.java      # 데이터 접근 계층
├── domain/
│   └── CarTracking.java                # 엔티티 클래스
├── dto/
│   ├── CarTrackingRequest.java         # 요청 DTO
│   └── CarTrackingResponse.java        # 응답 DTO
└── exception/
    └── GlobalExceptionHandler.java     # 전역 예외 처리
```

## API 엔드포인트

### 1. 차량 위치 정보 수신 (DTG → 서버)
```
POST /api/car-tracking/receive
Content-Type: application/json

{
  "vehicleId": "CAR001",
  "vehicleName": "택시-001",
  "latitude": 37.5665,
  "longitude": 126.9780,
  "speed": 35.0,
  "heading": 90.0,
  "status": "RUNNING",
  "timestamp": "2024-01-15T10:30:00",
  "fuelLevel": 85.0,
  "engineStatus": "ON"
}
```

### 2. 모든 차량의 현재 위치 조회 (실시간 지도 표시용)
```
GET /api/car-tracking/current-locations
```

### 3. 특정 차량의 최신 위치 조회
```
GET /api/car-tracking/vehicle/{vehicleId}/latest
```

### 4. 특정 차량의 위치 이력 조회
```
GET /api/car-tracking/vehicle/{vehicleId}/history
```

### 5. 시간 범위별 차량 위치 조회
```
GET /api/car-tracking/locations/time-range?startTime=2024-01-15T10:00:00&endTime=2024-01-15T11:00:00
```

### 6. 지역별 차량 조회
```
GET /api/car-tracking/locations/area?minLat=37.5660&maxLat=37.5680&minLng=126.9770&maxLng=126.9800
```

### 7. 상태별 차량 조회
```
GET /api/car-tracking/vehicles/status/RUNNING
```

### 8. 차량 상태 업데이트
```
PUT /api/car-tracking/vehicle/{vehicleId}/status?status=MAINTENANCE
```

### 9. 헬스체크
```
GET /api/car-tracking/health
```

## 데이터베이스 설정

### PostgreSQL 데이터베이스 생성
```sql
CREATE DATABASE car_tracking_db;
CREATE USER postgres WITH PASSWORD 'password';
GRANT ALL PRIVILEGES ON DATABASE car_tracking_db TO postgres;
```

### application.yml 설정
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/car_tracking_db
    username: postgres
    password: password
```

## 실행 방법

### 1. 의존성 설치
```bash
./gradlew build
```

### 2. 애플리케이션 실행
```bash
./gradlew bootRun
```

### 3. 테스트
```bash
./gradlew test
```

## 테스트 데이터

애플리케이션 시작 시 자동으로 3대의 차량에 대한 테스트 데이터가 생성됩니다:

- **CAR001**: 서울시청 근처에서 운행 중 (속도: 35km/h)
- **CAR002**: 서울시청 근처에서 정차 중 (정지 상태)
- **CAR003**: 서울시청 근처에서 운행 중 (속도: 45km/h)

## 프론트엔드 연동

### 실시간 업데이트
현재는 REST API만 제공하지만, 향후 WebSocket을 추가하여 실시간 업데이트를 지원할 예정입니다.

### CORS 설정
프론트엔드에서 접근할 수 있도록 CORS가 설정되어 있습니다.

## 모니터링 및 로깅

- 애플리케이션 로그: `DEBUG` 레벨로 설정
- SQL 쿼리 로그: 활성화됨
- 헬스체크 엔드포인트: `/api/car-tracking/health`

## 향후 개선 사항

1. **WebSocket 지원**: 실시간 차량 위치 업데이트
2. **지오펜싱**: 특정 지역 진입/이탈 알림
3. **경로 추적**: 차량 이동 경로 시각화
4. **성능 최적화**: Redis 캐싱, 데이터베이스 인덱싱
5. **보안**: API 키 인증, HTTPS 지원
6. **모니터링**: Prometheus, Grafana 연동
