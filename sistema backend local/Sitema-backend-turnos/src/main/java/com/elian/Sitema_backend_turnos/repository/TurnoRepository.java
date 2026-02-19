package com.elian.Sitema_backend_turnos.repository;

import com.elian.Sitema_backend_turnos.model.EstadoTurno;
import com.elian.Sitema_backend_turnos.model.Turno;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface TurnoRepository extends JpaRepository<Turno, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Turno> findByProfesionalIdAndFechaAndHora(Long profesionalId, LocalDate fecha, LocalTime hora);
    // Buscar todos los turnos de un profesional en una fecha (sin filtrar por hora)
    List<Turno> findByProfesionalIdAndFecha(Long profesionalId, LocalDate fecha);
    List<Turno> findByClienteId(Long clienteId);
    List<Turno> findByClienteIdAndFechaAndEstado(Long clienteId, LocalDate fecha, EstadoTurno estado);
    List<Turno> findByClienteIdAndEstado(Long clienteId, EstadoTurno estado);
    List<Turno> findByClienteIdAndFecha(Long clienteId, LocalDate fecha);
    List<Turno> findByEstado(EstadoTurno estado);
    List<Turno> findByProfesionalId(Long profesionalId);
    List<Turno> findByFecha(LocalDate fecha);
    List<Turno> findByProfesionalIdAndFechaOrderByHoraAsc(Long profesionalId, LocalDate fecha);
    List<Turno> findByProfesionalIdAndClienteNombreOrderByFechaDesc(Long profesionalId, String clienteNombre);
    List<Turno> findByProfesionalIdOrderByFechaDescHoraAsc(Long profesionalId);
    List<Turno> findByFechaAndEstadoIn(LocalDate fecha, List<EstadoTurno> estados);

    @Query("""
SELECT t FROM Turno t
WHERE (:estado IS NULL OR t.estado = :estado)
AND (:profesionalId IS NULL OR t.profesional.id = :profesionalId)
AND (:fecha IS NULL OR t.fecha = :fecha)
""")
    Page<Turno> buscarConFiltros(
            @Param("estado") EstadoTurno estado,
            @Param("profesionalId") Long profesionalId,
            @Param("fecha") LocalDate fecha,
            Pageable pageable);
}


