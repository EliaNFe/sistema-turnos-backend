package com.elian.Sitema_backend_turnos.service;

import com.elian.Sitema_backend_turnos.dto.ActualizarProfesionalDTO;
import com.elian.Sitema_backend_turnos.dto.CrearProfesionalDTO;
import com.elian.Sitema_backend_turnos.dto.ProfesionalDTO;
import com.elian.Sitema_backend_turnos.exception.ProfesionalNotFoundException;
import com.elian.Sitema_backend_turnos.mapper.ProfesionalMapper;
import com.elian.Sitema_backend_turnos.model.Profesional;
import com.elian.Sitema_backend_turnos.repository.ProfesionalRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProfesionalService {

    private final ProfesionalRepository profesionalRepository;
    private final ProfesionalMapper ProfesionalMapper;

    public ProfesionalService(ProfesionalRepository profesionalRepository, ProfesionalMapper profesionalMapper) {
        this.profesionalRepository = profesionalRepository;
        ProfesionalMapper = profesionalMapper;
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
                p.getEspecialidad(),
                p.isActivo()
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


    public void desactivar(Long id) {
        Profesional p = profesionalRepository.findById(id)
                .orElseThrow(() -> new ProfesionalNotFoundException(id));

        p.setActivo(false);
        profesionalRepository.save(p);
    }

    public void activar(Long id) {
        Profesional p = profesionalRepository.findById(id)
                .orElseThrow(() -> new ProfesionalNotFoundException(id));

        p.setActivo(true);
        profesionalRepository.save(p);
    }


    // En ProfesionalService.java
    public List<ProfesionalDTO> listarDisponiblesParaUsuario() {
        // Buscamos profesionales que NO estén presentes en la tabla de usuarios
        return profesionalRepository.findProfesionalesSinUsuario()
                .stream()
                .map(ProfesionalMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void guardarProfesional(CrearProfesionalDTO dto) {
        Profesional p = new Profesional();
        p.setNombre(dto.nombre());
        p.setEspecialidad(dto.especialidad());
        p.setActivo(true); // Por defecto arranca a laburar
        profesionalRepository.save(p);
    }

    // Y para que en los combos de "Nuevo Turno" no aparezcan los inactivos:
    public List<Profesional> listarActivos() {
        return profesionalRepository.findAll()
                .stream()
                .filter(Profesional::isActivo)
                .toList();
    }
}

