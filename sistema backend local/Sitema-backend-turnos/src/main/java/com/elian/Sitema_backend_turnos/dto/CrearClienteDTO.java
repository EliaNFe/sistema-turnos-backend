package com.elian.Sitema_backend_turnos.dto;

public record CrearClienteDTO(
        String nombre,
        String apellido,
        String documento,
        String telefono,
        String email
) {}
