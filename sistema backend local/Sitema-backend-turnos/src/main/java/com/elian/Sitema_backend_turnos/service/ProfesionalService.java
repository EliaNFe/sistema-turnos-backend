package com.elian.Sitema_backend_turnos.service;

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

    public Profesional crearProfesional(Profesional profesional) {

        if (profesionalRepository.existsByNombreAndEspecialidad(
                profesional.getNombre(),
                profesional.getEspecialidad())) {

            throw new IllegalArgumentException(
                    "Ya existe un profesional con ese nombre y especialidad");
        }

        return profesionalRepository.save(profesional);
    }

    public List<Profesional> listarProfesionales() {
        return profesionalRepository.findAll();
    }

    public Profesional buscarPorId(Long id) {
        return profesionalRepository.findById(id)
                .orElseThrow(() -> new ProfesionalNotFoundException(id));
    }

    public Profesional actualizar(Long id, Profesional profesional) {
        Profesional existente = profesionalRepository.findById(id)
                .orElseThrow(() -> new ProfesionalNotFoundException(id));

        existente.setNombre(profesional.getNombre());
        existente.setEspecialidad(profesional.getEspecialidad());

        return profesionalRepository.save(existente);
    }

    public void eliminar(Long id) {
        if (!profesionalRepository.existsById(id)) {
            throw new ProfesionalNotFoundException(id);
        }
        profesionalRepository.deleteById(id);
    }
}

