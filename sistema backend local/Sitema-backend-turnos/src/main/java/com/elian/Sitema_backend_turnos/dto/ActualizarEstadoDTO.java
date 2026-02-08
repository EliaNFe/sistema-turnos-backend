package com.elian.Sitema_backend_turnos.dto;

import com.elian.Sitema_backend_turnos.model.EstadoTurno;
import jakarta.validation.constraints.NotNull;

public class ActualizarEstadoDTO {
    @NotNull
    private EstadoTurno estado;

    public EstadoTurno getEstado() {
        return estado;
    }

    public void setEstado(EstadoTurno estado) {
        this.estado = estado;
    }
}
