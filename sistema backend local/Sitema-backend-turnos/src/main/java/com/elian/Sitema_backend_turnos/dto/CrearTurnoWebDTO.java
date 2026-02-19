package com.elian.Sitema_backend_turnos.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record CrearTurnoWebDTO(
        String nombre,
        String apellido,
        String documento,
        String telefono,
        String email,
        LocalDate fecha,
        LocalTime hora
) {}
