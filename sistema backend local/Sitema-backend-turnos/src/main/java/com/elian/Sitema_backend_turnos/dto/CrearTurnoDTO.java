package com.elian.Sitema_backend_turnos.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

public record CrearTurnoDTO(

        @NotNull(message = "clienteId obligatorio")
        Long clienteId,

        @NotNull(message = "profesionalId obligatorio")
        Long profesionalId,

        @NotNull(message = "fecha obligatoria")
        @FutureOrPresent(message = "la fecha no puede ser pasada")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate fecha,

        @NotNull(message = "hora obligatoria")
        @DateTimeFormat(pattern = "HH:mm")
        LocalTime hora
) {}
