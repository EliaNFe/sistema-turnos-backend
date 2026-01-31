package com.elian.Sitema_backend_turnos.exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ClientenotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleClienteNotFound(
            ClientenotFoundException ex) {

        Map<String, Object> error = new HashMap<>();
        error.put("error", "Cliente no encontrado");
        error.put("mensaje", ex.getMessage());
        error.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}