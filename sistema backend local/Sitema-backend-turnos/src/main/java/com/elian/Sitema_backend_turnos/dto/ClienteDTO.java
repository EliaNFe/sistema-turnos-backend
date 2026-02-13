package com.elian.Sitema_backend_turnos.dto;

public record ClienteDTO(
        String nombre,
        String apellido,
        String documento,
        String telefono,
        String email,
        Long id,
        boolean activo
) {}


