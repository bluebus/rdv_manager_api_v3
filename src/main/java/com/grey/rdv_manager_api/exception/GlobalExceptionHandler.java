package com.grey.rdv_manager_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Catches all @Validated failures and returns a clean 400 JSON body
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex) {

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .toList();

        return ResponseEntity.badRequest().body(Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status",    400,
                "errors",    errors
        ));
    }

    // Catches compact constructor ValidationException from CreateSlotRequest
    @ExceptionHandler(jakarta.validation.ValidationException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            jakarta.validation.ValidationException ex) {

        return ResponseEntity.badRequest().body(Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status",    400,
                "errors",    List.of(ex.getMessage())
        ));
    }

    // Catches RuntimeException from service layer (not found etc.)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status",    404,
                "errors",    List.of(ex.getMessage())
        ));
    }
}