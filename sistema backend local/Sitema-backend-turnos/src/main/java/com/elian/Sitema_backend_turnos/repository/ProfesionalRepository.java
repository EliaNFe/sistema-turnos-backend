package com.elian.Sitema_backend_turnos.repository;

import com.elian.Sitema_backend_turnos.model.Profesional;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProfesionalRepository extends JpaRepository<Profesional, Long> {

    boolean existsByNombreAndEspecialidad(String nombre, String especialidad);

    ;
}