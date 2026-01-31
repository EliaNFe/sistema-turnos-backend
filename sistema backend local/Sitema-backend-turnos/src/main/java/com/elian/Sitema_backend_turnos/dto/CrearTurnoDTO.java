package com.elian.Sitema_backend_turnos.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record CrearTurnoDTO(
        Long clienteId,
        Long profesionalId,
        LocalDate fecha,
        LocalTime hora
) {}
