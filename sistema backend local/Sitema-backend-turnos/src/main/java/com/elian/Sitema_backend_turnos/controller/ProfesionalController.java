package com.elian.Sitema_backend_turnos.controller;

import com.elian.Sitema_backend_turnos.dto.CrearProfesionalDTO;
import com.elian.Sitema_backend_turnos.dto.ProfesionalDTO;
import com.elian.Sitema_backend_turnos.service.ProfesionalService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profesionales")
public class ProfesionalController {

    private final ProfesionalService profesionalService;

    public ProfesionalController(
            ProfesionalService profesionalService) {

        this.profesionalService = profesionalService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProfesionalDTO> crear(
            @Valid @RequestBody CrearProfesionalDTO dto) {

        return ResponseEntity.ok(
                profesionalService.crearProfesional(dto)
        );
    }
}
