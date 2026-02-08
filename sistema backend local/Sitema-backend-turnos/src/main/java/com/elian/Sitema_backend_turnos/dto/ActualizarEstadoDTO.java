package com.elian.Sitema_backend_turnos.dto;

import com.elian.Sitema_backend_turnos.model.EstadoTurno;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ActualizarEstadoDTO {

    @NotNull(message = "Estado requerido")
    private EstadoTurno estado;

}
