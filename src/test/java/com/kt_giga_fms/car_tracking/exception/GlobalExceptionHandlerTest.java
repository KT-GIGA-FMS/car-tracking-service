package com.kt_giga_fms.car_tracking.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GlobalExceptionHandler 테스트")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("NotFoundException 처리 - 성공")
    void handleNotFound_Success() {
        // given
        NotFoundException exception = new NotFoundException("Vehicle not found: TEST001");

        // when
        ResponseEntity<Object> response = globalExceptionHandler.handleNotFound(exception);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body.get("status")).isEqualTo(404);
        assertThat(body.get("message")).isEqualTo("Not found");
        assertThat(body.get("error")).isEqualTo("Vehicle not found: TEST001");
    }

    @Test
    @DisplayName("BadRequestException 처리 - 성공")
    void handleBadRequest_Success() {
        // given
        BadRequestException exception = new BadRequestException("Invalid request parameters");

        // when
        ResponseEntity<Object> response = globalExceptionHandler.handleBadRequest(exception);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body.get("status")).isEqualTo(400);
        assertThat(body.get("message")).isEqualTo("Bad request");
        assertThat(body.get("error")).isEqualTo("Invalid request parameters");
    }

    @Test
    @DisplayName("일반 Exception 처리 - 성공")
    void handleGlobal_Success() {
        // given
        Exception exception = new Exception("Internal server error");

        // when
        ResponseEntity<Object> response = globalExceptionHandler.handleGlobal(exception);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body.get("status")).isEqualTo(500);
        assertThat(body.get("message")).isEqualTo("Internal server error");
        assertThat(body.get("error")).isEqualTo("Internal server error");
    }

    @Test
    @DisplayName("IllegalArgumentException 처리 - 성공")
    void handleIllegalArgument_Success() {
        // given
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");

        // when
        ResponseEntity<Object> response = globalExceptionHandler.handleIllegalArgument(exception);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body.get("status")).isEqualTo(400);
        assertThat(body.get("message")).isEqualTo("Invalid argument");
        assertThat(body.get("error")).isEqualTo("Invalid argument");
    }
}
