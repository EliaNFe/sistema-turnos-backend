package com.elian.Sitema_backend_turnos.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record CrearTurnoDTO(
        Long clienteId,
        Long profesionalId,
        @NotNull(message = "La fecha no puede ser nula")
        LocalDate fecha,
        LocalTime hora
) {}
