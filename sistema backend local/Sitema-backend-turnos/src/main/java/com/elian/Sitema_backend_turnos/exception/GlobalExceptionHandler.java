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

    // ========= Cliente =========

    @ExceptionHandler(ClientenotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleClienteNotFound(
            ClientenotFoundException ex) {

        return buildResponse("Cliente no encontrado", ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // ========= Turno =========

    @ExceptionHandler(TurnoNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleTurnoNotFound(
            TurnoNotFoundException ex) {

        return buildResponse("Turno no encontrado", ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // ========= Validaciones =========

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(
            IllegalArgumentException ex) {

        return buildResponse("Datos inválidos", ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // ========= Fallback =========

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {

        return buildResponse("Error interno", ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ========= Helper =========

    private ResponseEntity<Map<String, Object>> buildResponse(
            String error,
            String mensaje,
            HttpStatus status) {

        Map<String, Object> body = new HashMap<>();

        body.put("error", error);
        body.put("mensaje", mensaje);
        body.put("status", status.value());
        body.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(status).body(body);
    }
}
