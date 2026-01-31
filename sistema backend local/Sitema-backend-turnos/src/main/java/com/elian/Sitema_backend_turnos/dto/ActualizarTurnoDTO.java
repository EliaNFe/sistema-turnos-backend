package com.elian.Sitema_backend_turnos.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record ActualizarTurnoDTO(
        Long profesionalId,  // opcional: no creo poner para cambiar profesional pero..... aca queda
        LocalDate fecha,
        LocalTime hora,
        String estado  // ejemplo: "PENDIENTE", "CANCELADO", "COMPLETADO"
) {}