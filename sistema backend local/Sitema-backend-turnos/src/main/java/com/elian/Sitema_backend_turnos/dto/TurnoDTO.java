package com.elian.Sitema_backend_turnos.dto;

import com.elian.Sitema_backend_turnos.model.EstadoTurno;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

public record TurnoDTO(
        Long id,
        LocalDate fecha,
        LocalTime hora,
        EstadoTurno estado,

        Long clienteId,
        String clienteNombre,

        Long profesionalId,
        String profesionalNombre
) {
    @Override
    public Long id() {
        return id;
    }

    @Override
    public LocalDate fecha() {
        return fecha;
    }

    @Override
    public LocalTime hora() {
        return hora;
    }

    @Override
    public EstadoTurno estado() {
        return estado;
    }

    @Override
    public Long clienteId() {
        return clienteId;
    }

    @Override
    public Long profesionalId() {
        return profesionalId;
    }

    @Override
    public String clienteNombre() {
        return clienteNombre;
    }

    @Override
    public String profesionalNombre() {
        return profesionalNombre;
    }
}

