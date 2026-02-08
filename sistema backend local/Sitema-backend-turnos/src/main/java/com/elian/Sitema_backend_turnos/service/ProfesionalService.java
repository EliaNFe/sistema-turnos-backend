package com.elian.Sitema_backend_turnos.service;

import com.elian.Sitema_backend_turnos.dto.ActualizarProfesionalDTO;
import com.elian.Sitema_backend_turnos.dto.CrearProfesionalDTO;
import com.elian.Sitema_backend_turnos.dto.ProfesionalDTO;
import com.elian.Sitema_backend_turnos.exception.ProfesionalNotFoundException;
import com.elian.Sitema_backend_turnos.model.Profesional;
import com.elian.Sitema_backend_turnos.repository.ProfesionalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfesionalService {

    private final ProfesionalRepository profesionalRepository;

    public ProfesionalService(ProfesionalRepository profesionalRepository) {
        this.profesionalRepository = profesionalRepository;
    }

    public ProfesionalDTO crearProfesional(CrearProfesionalDTO dto) {

        Profesional profesional = new Profesional();
        profesional.setNombre(dto.nombre());
        profesional.setEspecialidad(dto.especialidad());


        return toDTO(
                profesionalRepository.save(profesional)
        );

    }


    public List<Profesional> listarProfesionales() {
        return profesionalRepository.findAll();
    }

    public Profesional buscarPorId(Long id) {
        return profesionalRepository.findById(id)
                .orElseThrow(() -> new ProfesionalNotFoundException(id));
    }

    private ProfesionalDTO toDTO(Profesional p) {
        return new ProfesionalDTO(
                p.getId(),
                p.getNombre(),
                p.getEspecialidad()
        );
    }

    public ProfesionalDTO actualizar(
            Long id,
            ActualizarProfesionalDTO dto) {

        Profesional p = profesionalRepository.findById(id)
                .orElseThrow(() -> new ProfesionalNotFoundException(id));

        p.setNombre(dto.nombre());
        p.setEspecialidad(dto.especialidad());

        return toDTO(
                profesionalRepository.save(p)
        );
    }


    public void eliminar(Long id) {
        if (!profesionalRepository.existsById(id)) {
            throw new ProfesionalNotFoundException(id);
        }
        profesionalRepository.deleteById(id);
    }
}

