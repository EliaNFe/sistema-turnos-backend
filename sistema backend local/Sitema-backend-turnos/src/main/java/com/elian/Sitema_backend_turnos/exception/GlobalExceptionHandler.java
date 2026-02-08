package com.elian.Sitema_backend_turnos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ========= Cliente =========

    @ExceptionHandler(ClientenotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCliente(
            ClientenotFoundException ex) {

        return build("Cliente no encontrado", ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // ========= Profesional =========

    @ExceptionHandler(ProfesionalNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleProfesional(
            ProfesionalNotFoundException ex) {

        return build("Profesional no encontrado", ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // ========= Turno =========

    @ExceptionHandler(TurnoNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleTurno(
            TurnoNotFoundException ex) {

        return build("Turno no encontrado", ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // ========= Validaciones =========

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegal(
            IllegalArgumentException ex) {

        return build("Datos inválidos", ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // ========= Error general =========

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {

        Map<String, Object> error = new HashMap<>();
        error.put("error", "Error interno");
        error.put("mensaje", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }

    // ========= Helper =========

    private ResponseEntity<Map<String, Object>> build(
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, Object> errores = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errores.put(error.getField(), error.getDefaultMessage())
                );

        return ResponseEntity
                .badRequest()
                .body(errores);
    }
}

