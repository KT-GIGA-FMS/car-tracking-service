package com.kt_giga_fms.car_tracking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

	private ResponseEntity<Object> build(HttpStatus status, String message, String error) {
		Map<String, Object> body = new HashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", status.value());
		body.put("message", message);
		body.put("error", error);
		return new ResponseEntity<>(body, status);
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<Object> handleBadRequest(BadRequestException ex) {
		return build(HttpStatus.BAD_REQUEST, "Bad request", ex.getMessage());
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<Object> handleNotFound(NotFoundException ex) {
		return build(HttpStatus.NOT_FOUND, "Not found", ex.getMessage());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex) {
		return build(HttpStatus.BAD_REQUEST, "Invalid argument", ex.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleGlobal(Exception ex) {
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", ex.getMessage());
	}
}
