package com.oskarvos.notesservice.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class CommonExceptionHandler {

    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_ERRORS = "errors";

    @ExceptionHandler(NoteNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NoteNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                KEY_TIMESTAMP, Instant.now(),
                KEY_STATUS, HttpStatus.NOT_FOUND.value(),
                KEY_MESSAGE, ex.getMessage()
        ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));

        Map<String, Object> body = new HashMap<>();
        body.put(KEY_TIMESTAMP, Instant.now());
        body.put(KEY_STATUS, HttpStatus.BAD_REQUEST.value());
        body.put(KEY_ERRORS, errors);

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraint(
            ConstraintViolationException ex) {
        Map<String, String> errors = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        v -> v.getPropertyPath().toString(),
                        ConstraintViolation::getMessage));
        return ResponseEntity.badRequest().body(Map.of(
                KEY_TIMESTAMP, Instant.now(),
                KEY_STATUS, HttpStatus.BAD_REQUEST.value(),
                KEY_ERRORS, errors));
    }
}