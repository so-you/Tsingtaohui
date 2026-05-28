package com.tsingtaohui.common.exception;

import com.tsingtaohui.common.model.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e, HttpServletRequest request) {
        String requestId = generateRequestId();
        log.warn("Business error [{}] {}: {}", requestId, e.getErrorCode(), e.getMessage());
        return ResponseEntity.ok(ApiResponse.error(e.getErrorCode(), e.getMessage(), requestId));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String requestId = generateRequestId();
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null
                ? fieldError.getField() + ": " + fieldError.getDefaultMessage()
                : "Validation failed";
        log.warn("Validation error [{}]: {}", requestId, message);
        return ResponseEntity.badRequest().body(ApiResponse.error("VALIDATION_ERROR", message, requestId));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException e, HttpServletRequest request) {
        String requestId = generateRequestId();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("AUTH_001", "Invalid username or password", requestId));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException e, HttpServletRequest request) {
        String requestId = generateRequestId();
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("AUTH_002", "Access denied", requestId));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e, HttpServletRequest request) {
        String requestId = generateRequestId();
        log.error("Unexpected error [{}]: {}", requestId, e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("INTERNAL_ERROR", "Internal server error", requestId));
    }

    private String generateRequestId() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
