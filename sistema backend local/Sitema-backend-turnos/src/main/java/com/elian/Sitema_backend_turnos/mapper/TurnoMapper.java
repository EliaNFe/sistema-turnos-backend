package com.elian.Sitema_backend_turnos.mapper;

import com.elian.Sitema_backend_turnos.dto.TurnoDTO;
import com.elian.Sitema_backend_turnos.model.Turno;

public class TurnoMapper {

    public static TurnoDTO toDTO(Turno turno) {
        return new TurnoDTO(
                turno.getId(),
                turno.getFecha(),
                turno.getHora(),
                turno.getEstado(),
                turno.getCliente().getId(),
                turno.getProfesional().getId()
        );
    }
}
