package com.elian.Sitema_backend_turnos.repository;

import com.elian.Sitema_backend_turnos.model.EstadoTurno;
import com.elian.Sitema_backend_turnos.model.Turno;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

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

}


