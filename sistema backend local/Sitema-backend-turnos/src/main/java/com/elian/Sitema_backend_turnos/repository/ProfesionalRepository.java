package com.elian.Sitema_backend_turnos.repository;

import com.elian.Sitema_backend_turnos.model.Profesional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ProfesionalRepository extends JpaRepository<Profesional, Long> {

    boolean existsByNombreAndEspecialidad(String nombre, String especialidad);
    @Query("SELECT p FROM Profesional p WHERE NOT EXISTS (SELECT u FROM Usuario u WHERE u.profesional = p)")
    List<Profesional> findProfesionalesSinUsuario();
    boolean existsByNombreIgnoreCaseAndEspecialidadIgnoreCase(String nombre, String especialidad);
    @Query("SELECT p FROM Profesional p WHERE (:nombre IS NULL OR p.nombre LIKE %:nombre%)")
    Page<Profesional> buscarProfesionales(@Param("nombre") String nombre, Pageable pageable);



}