package com.elian.Sitema_backend_turnos.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record CrearTurnoDTO(

        @NotNull(message = "clienteId obligatorio")
        Long clienteId,

        @NotNull(message = "profesionalId obligatorio")
        Long profesionalId,

        @NotNull(message = "fecha obligatoria")
        @FutureOrPresent(message = "la fecha no puede ser pasada")
        LocalDate fecha,

        @NotNull(message = "hora obligatoria")
        LocalTime hora
) {}
