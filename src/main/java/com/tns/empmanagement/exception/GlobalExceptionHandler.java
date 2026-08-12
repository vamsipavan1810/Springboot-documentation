package com.tns.empmanagement.exception;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException e, HttpServletRequest request) {
		log.warn("Resource not found: {}", e.getMessage());
		ErrorResponse response = new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), e.getMessage(), List.of(), request.getRequestURI());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}
	
	@ExceptionHandler(DuplicateResourceException.class)
	public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateResourceException e, HttpServletRequest request) {
		log.warn("Duplicate resource: {}", e.getMessage());
		ErrorResponse response = new ErrorResponse(LocalDateTime.now(), HttpStatus.CONFLICT.value(), e.getMessage(), List.of(), request.getRequestURI());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	}
	
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ErrorResponse> handleBusiness(BusinessException e, HttpServletRequest request) {
		log.warn("Business rule violation: {}", e.getMessage());
		ErrorResponse response = new ErrorResponse(LocalDateTime.now(), HttpStatus.UNPROCESSABLE_ENTITY.value(), e.getMessage(), List.of(), request.getRequestURI());
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e, HttpServletRequest request) {
		List<String> errors = e.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(FieldError::getDefaultMessage)
				.toList();
		log.warn("Validation failed: {}", errors);
		ErrorResponse response = new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), "Validation Failed", errors, request.getRequestURI());
		return ResponseEntity.badRequest().body(response);
	}
	
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e, HttpServletRequest request) {
		log.warn(e.getMessage());
		ErrorResponse response = new ErrorResponse(LocalDateTime.now(), 400, e.getMessage(), null, request.getRequestURI());
		return ResponseEntity.badRequest().body(response);
	}
	
	@ExceptionHandler(DataAccessException.class)
	public ResponseEntity<ErrorResponse> handleDatabaseException(DataAccessException e, HttpServletRequest request) {
		log.error("Database connection problem", e);
		ErrorResponse response = new ErrorResponse(LocalDateTime.now(), HttpStatus.SERVICE_UNAVAILABLE.value(), "Database service is temporarily unavailable. Please try again later.", List.of(), request.getRequestURI());
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGeneric(Exception e, HttpServletRequest request) {
		log.error("Unexpected exception", e);
		ErrorResponse response = new ErrorResponse(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(), "Something went wrong from server side", List.of(), request.getRequestURI());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}
}
