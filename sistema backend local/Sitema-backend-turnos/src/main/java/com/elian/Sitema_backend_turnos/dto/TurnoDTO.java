package com.elian.Sitema_backend_turnos.dto;

import com.elian.Sitema_backend_turnos.model.EstadoTurno;

import java.time.LocalDate;
import java.time.LocalTime;

public record TurnoDTO(
        Long id,
        LocalDate fecha,
        LocalTime hora,
        EstadoTurno estado,
        Long clienteId,
        Long profesionalId
) {}

