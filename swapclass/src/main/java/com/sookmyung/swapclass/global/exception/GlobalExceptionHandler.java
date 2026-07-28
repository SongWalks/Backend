package com.sookmyung.swapclass.global.exception;

import com.sookmyung.swapclass.global.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice(basePackages = "com.sookmyung.swapclass")
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<?>> handleCustomException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.fail(errorCode.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .findFirst()
                .orElse("잘못된 입력값입니다.");
        return ResponseEntity
                .status(400)
                .body(ApiResponse.fail(message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        // 처리되지 않은 예외의 실제 원인을 로그로 남김(500 디버깅용)
        log.error("Unhandled exception", e);
        return ResponseEntity
                .status(500)
                .body(ApiResponse.fail("서버 내부 오류입니다."));
    }

    @ExceptionHandler(SuspendedUserException.class)
    public ResponseEntity<ApiResponse<?>> handleSuspendedException(SuspendedUserException e) {
        return ResponseEntity
                .status(403)
                .body(ApiResponse.fail(e.getMessage(), Map.of("suspendedUntil", e.getSuspendedUntil())));
    }
}

