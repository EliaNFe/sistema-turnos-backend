package com.elian.Sitema_backend_turnos.dto;

import jakarta.validation.constraints.NotBlank;

public record CrearProfesionalDTO(

        @NotBlank
        String nombre,
        @NotBlank
        String especialidad
) {}
