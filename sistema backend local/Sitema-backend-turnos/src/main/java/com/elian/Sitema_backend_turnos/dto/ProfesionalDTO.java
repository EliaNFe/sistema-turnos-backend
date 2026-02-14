package com.elian.Sitema_backend_turnos.dto;

public record ProfesionalDTO(
        Long id,
        String nombre,
        String especialidad,
        boolean activo,
        boolean tieneUsuario
) {}
