package com.elian.Sitema_backend_turnos.mapper;

import com.elian.Sitema_backend_turnos.dto.ProfesionalDTO;
import com.elian.Sitema_backend_turnos.dto.UsuarioLogueadoDTO;
import com.elian.Sitema_backend_turnos.model.Profesional;
import com.elian.Sitema_backend_turnos.service.UsuarioService;
import org.springframework.stereotype.Component;

@Component
public class ProfesionalMapper {
    private final UsuarioService usuarioService;

    public ProfesionalMapper(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    public ProfesionalDTO toDTO(Profesional profesional) {
        if (profesional == null) return null;


        return new ProfesionalDTO(
                profesional.getId(),
                profesional.getNombre(),
                profesional.getEspecialidad(),
                profesional.isActivo(),
                usuarioService.tieneUsuarioAsociado(profesional.getId())
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