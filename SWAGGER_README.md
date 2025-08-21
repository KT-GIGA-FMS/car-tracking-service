# Car Tracking Service Swagger API 문서

## 개요
Car Tracking Service에 Swagger UI가 통합되어 차량 추적 및 위치 모니터링 API 문서를 쉽게 확인하고 테스트할 수 있습니다.

## 접근 방법

### 1. Swagger UI
- **URL**: `http://localhost:8080/swagger-ui.html`
- **설명**: 웹 기반 API 문서 및 테스트 인터페이스

### 2. OpenAPI JSON
- **URL**: `http://localhost:8080/api-docs`
- **설명**: OpenAPI 3.0 스펙에 따른 JSON 형태의 API 문서

## 주요 기능

### API 그룹

#### 1. Car Tracking (차량 추적)
- **차량 위치 정보 수신**: DTG로부터 실시간 위치 정보 수신
- **전체 차량 현재 위치 조회**: 실시간 지도 표시용
- **차량별 최신 위치 조회**: 특정 차량의 최신 위치 정보
- **차량 위치 이력 조회**: 특정 차량의 위치 이력
- **시간 범위별 위치 조회**: 특정 시간 범위 내의 위치 정보
- **지역별 차량 조회**: 특정 지역 내의 차량들
- **상태별 차량 조회**: 특정 상태의 차량들
- **차량 상태 업데이트**: 차량 상태 변경
- **헬스 체크**: 서비스 상태 확인

## 상세 정보
각 API 엔드포인트는 다음 정보를 포함합니다:
- **요약**: API 기능 간단 설명
- **상세 설명**: API 동작 방식 상세 설명
- **요청/응답 스키마**: DTO 클래스 구조
- **응답 코드**: HTTP 상태 코드별 설명
- **예시 값**: 실제 사용 예시

## DTO 문서화

### 주요 DTO 클래스

#### 1. CarTrackingRequest
- **설명**: 차량 위치 추적 요청 DTO
- **주요 필드**: vehicleId, latitude, longitude, speed, heading, status, timestamp
- **용도**: DTG로부터 차량 위치 정보 수신

#### 2. CarTrackingResponse
- **설명**: 차량 위치 추적 응답 DTO
- **주요 필드**: vehicleId, vehicleName, latitude, longitude, speed, status
- **용도**: 차량 위치 정보 응답

#### 3. ApiListResponse<T>
- **설명**: API 리스트 응답 공통 DTO
- **주요 필드**: data, totalCount, success
- **용도**: 리스트 형태의 데이터 응답

## 설정

### SwaggerConfig.java
- API 정보 (제목, 설명, 버전, 연락처, 라이선스)
- 서버 정보 (로컬, 프로덕션)

### application.yml
- API 문서 경로 설정
- Swagger UI 커스터마이징 옵션
- 정렬 및 표시 옵션

## 사용 예시

### 1. 차량 위치 정보 수신
```bash
POST /api/v1/car-tracking/receive
Content-Type: application/json

{
  "vehicleId": "CAR001",
  "vehicleName": "현대 아반떼",
  "latitude": 37.5665,
  "longitude": 126.9780,
  "speed": 60.5,
  "heading": 180.0,
  "status": "ACTIVE",
  "timestamp": "2024-01-15T09:00:00",
  "fuelLevel": 75.5,
  "engineStatus": "RUNNING"
}
```

### 2. 전체 차량 현재 위치 조회
```bash
GET /api/v1/car-tracking/current-locations
```

### 3. 차량별 최신 위치 조회
```bash
GET /api/v1/car-tracking/vehicle/CAR001/latest
```

### 4. 차량 위치 이력 조회
```bash
GET /api/v1/car-tracking/vehicle/CAR001/history
```

### 5. 시간 범위별 위치 조회
```bash
GET /api/v1/car-tracking/locations/time-range?startTime=2024-01-15T09:00:00&endTime=2024-01-15T17:00:00
```

### 6. 지역별 차량 조회
```bash
GET /api/v1/car-tracking/locations/area?minLat=37.5&maxLat=37.6&minLng=126.9&maxLng=127.0
```

### 7. 상태별 차량 조회
```bash
GET /api/v1/car-tracking/vehicles/status/ACTIVE
```

### 8. 차량 상태 업데이트
```bash
PUT /api/v1/car-tracking/vehicle/CAR001/status?status=MAINTENANCE
```

### 9. 헬스 체크
```bash
GET /api/v1/car-tracking/health
```

## 데이터 구조

### 위치 정보 데이터 예시
```json
{
  "vehicleId": "CAR001",
  "vehicleName": "현대 아반떼",
  "latitude": 37.5665,
  "longitude": 126.9780,
  "speed": 60.5,
  "heading": 180.0,
  "status": "ACTIVE",
  "timestamp": "2024-01-15T09:00:00",
  "fuelLevel": 75.5,
  "engineStatus": "RUNNING"
}
```

### 리스트 응답 데이터 예시
```json
{
  "data": [
    {
      "vehicleId": "CAR001",
      "vehicleName": "현대 아반떼",
      "latitude": 37.5665,
      "longitude": 126.9780,
      "speed": 60.5,
      "status": "ACTIVE"
    }
  ],
  "totalCount": 1,
  "success": true
}
```

## 주요 기능

### 실시간 추적
- WebSocket을 통한 실시간 차량 위치 업데이트
- Redis를 활용한 빠른 위치 정보 조회
- 지도 기반 시각화 지원

### 위치 분석
- 시간대별, 지역별 차량 분포 분석
- 차량 상태별 모니터링
- 연료 레벨 및 엔진 상태 추적

### 데이터 저장
- PostgreSQL을 통한 영구 데이터 저장
- Flyway를 통한 데이터베이스 마이그레이션
- 배치 처리를 통한 대용량 데이터 처리

## 보안

- 모든 API는 적절한 인증 및 권한 검증이 필요합니다
- Swagger UI는 개발 환경에서만 활성화하는 것을 권장합니다
- 위치 정보는 민감한 데이터이므로 접근 제어가 중요합니다

## 문제 해결

### Swagger UI가 표시되지 않는 경우
1. 애플리케이션이 정상적으로 실행되었는지 확인
2. 포트 8080이 올바르게 설정되었는지 확인
3. 의존성이 올바르게 추가되었는지 확인

### API 문서가 업데이트되지 않는 경우
1. 애플리케이션 재시작
2. 캐시 클리어
3. 브라우저 새로고침

## 추가 정보

- **Spring Boot Version**: 3.5.4
- **SpringDoc OpenAPI Version**: 2.3.0
- **Java Version**: 17
- **OpenAPI Specification**: 3.0
- **주요 기능**: 실시간 차량 추적, 위치 모니터링, WebSocket 통신, Redis 캐싱
