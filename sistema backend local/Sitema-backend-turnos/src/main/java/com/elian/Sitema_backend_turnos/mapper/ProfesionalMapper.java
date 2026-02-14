package com.elian.Sitema_backend_turnos.mapper;

import com.elian.Sitema_backend_turnos.dto.ProfesionalDTO;
import com.elian.Sitema_backend_turnos.model.Profesional;
import org.springframework.stereotype.Component;

@Component
public class ProfesionalMapper {

    public ProfesionalDTO toDTO(Profesional profesional) {
        if (profesional == null) return null;

        // Aquí usas el constructor de tu Record o DTO
        return new ProfesionalDTO(
                profesional.getId(),
                profesional.getNombre(),
                profesional.getEspecialidad(),
                profesional.isActivo()
        );
    }

    // Si necesitas el camino inverso (de pantalla a base de datos)
    public Profesional toEntity(ProfesionalDTO dto) {
        if (dto == null) return null;

        Profesional profesional = new Profesional();
        profesional.setId(dto.id());
        profesional.setNombre(dto.nombre());
        profesional.setEspecialidad(dto.especialidad());
        profesional.setActivo(dto.activo());
        return profesional;
    }
}