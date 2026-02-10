package com.elian.Sitema_backend_turnos.mapper;

import com.elian.Sitema_backend_turnos.dto.ActualizarTurnoDTO;
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

    public static ActualizarTurnoDTO toActualizarDTO(Turno t) {

        return new ActualizarTurnoDTO(
                t.getProfesional().getId(),
                t.getFecha(),
                t.getHora(),
                t.getEstado().toString(),
                t.getId()
                );
}
}
