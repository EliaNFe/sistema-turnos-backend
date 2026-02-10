package com.elian.Sitema_backend_turnos.mapper;

import com.elian.Sitema_backend_turnos.dto.ActualizarTurnoDTO;
import com.elian.Sitema_backend_turnos.dto.TurnoDTO;
import com.elian.Sitema_backend_turnos.model.Turno;

public class TurnoMapper {

    public static TurnoDTO toDTO(Turno t) {
        return new TurnoDTO(
                t.getId(),
                t.getFecha(),
                t.getHora(),
                t.getEstado(),

                t.getCliente().getId(),
                t.getCliente().getNombre(),

                t.getProfesional().getId(),
                t.getProfesional().getNombre()
        );
    }

    public static ActualizarTurnoDTO toActualizarDTO(TurnoDTO t) {

        return new ActualizarTurnoDTO(
                t.profesionalId(),
                t.fecha(),
                t.hora(),
                t.estado().toString(),
                t.id()
                );
}
}
