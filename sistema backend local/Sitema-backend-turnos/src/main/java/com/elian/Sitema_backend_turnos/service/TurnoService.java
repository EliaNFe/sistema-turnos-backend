package com.elian.Sitema_backend_turnos.service;

import com.elian.Sitema_backend_turnos.dto.ActualizarTurnoDTO;
import com.elian.Sitema_backend_turnos.dto.CrearTurnoDTO;
import com.elian.Sitema_backend_turnos.dto.TurnoDTO;
import com.elian.Sitema_backend_turnos.exception.ClientenotFoundException;
import com.elian.Sitema_backend_turnos.exception.ProfesionalNotFoundException;
import com.elian.Sitema_backend_turnos.exception.TurnoNotFoundException;
import com.elian.Sitema_backend_turnos.mapper.TurnoMapper;
import com.elian.Sitema_backend_turnos.model.Cliente;
import com.elian.Sitema_backend_turnos.model.EstadoTurno;
import com.elian.Sitema_backend_turnos.model.Profesional;
import com.elian.Sitema_backend_turnos.model.Turno;
import com.elian.Sitema_backend_turnos.repository.ClienteRepository;
import com.elian.Sitema_backend_turnos.repository.ProfesionalRepository;
import com.elian.Sitema_backend_turnos.repository.TurnoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TurnoService {

    private final TurnoRepository turnoRepository;
    private final ClienteRepository clienteRepository;
    private final ProfesionalRepository profesionalRepository;

    public TurnoService(
            TurnoRepository turnoRepository,
            ClienteRepository clienteRepository,
            ProfesionalRepository profesionalRepository
    ) {
        this.turnoRepository = turnoRepository;
        this.clienteRepository = clienteRepository;
        this.profesionalRepository = profesionalRepository;
    }

    @Transactional
    public TurnoDTO crearTurno(CrearTurnoDTO dto) {
        Profesional profesional = profesionalRepository.findById(dto.profesionalId())
                .orElseThrow(() -> new ProfesionalNotFoundException(dto.profesionalId()));

        Cliente cliente = clienteRepository.findById(dto.clienteId())
                .orElseThrow(() -> new ClientenotFoundException(dto.clienteId()));

        // Bloqueo pesimista para concurrencia
        Optional<Turno> turnoExistente = turnoRepository.findByProfesionalIdAndFechaAndHora(
                profesional.getId(), dto.fecha(), dto.hora()
        );

        if (turnoExistente.isPresent()) {
            throw new IllegalStateException("El profesional ya tiene un turno en ese horario");
        }

        Turno turno = new Turno();
        turno.setCliente(cliente);
        turno.setProfesional(profesional);
        turno.setFecha(dto.fecha());
        turno.setHora(dto.hora());
        turno.setEstado(EstadoTurno.PENDIENTE);

        return TurnoMapper.toDTO(turnoRepository.save(turno));
    }


    public TurnoDTO actualizarTurno(Long turnoId, ActualizarTurnoDTO dto) {
        // Buscar el turno existente
        Turno turno = turnoRepository.findById(turnoId)
                .orElseThrow(() -> new TurnoNotFoundException(turnoId));

        // Si cambiamos de profesional, buscamos el nuevo profesional
        if (dto.profesionalId() != null && !dto.profesionalId().equals(turno.getProfesional().getId())) {
            Profesional profesional = profesionalRepository.findById(dto.profesionalId())
                    .orElseThrow(() -> new ProfesionalNotFoundException(dto.profesionalId()));
            turno.setProfesional(profesional);
        }

        // Validar que la nueva fecha + hora no genere conflicto
        Optional<Turno> conflicto = turnoRepository.findByProfesionalIdAndFechaAndHora(
                turno.getProfesional().getId(),
                dto.fecha(),
                dto.hora()
        );

        if (conflicto.isPresent() && !conflicto.get().getId().equals(turnoId)) {
            throw new IllegalStateException("El profesional ya tiene un turno en ese horario");
        }

        // Actualizar campos
        turno.setFecha(dto.fecha());
        turno.setHora(dto.hora());

        if (dto.estado() != null) {
            turno.setEstado(EstadoTurno.valueOf(dto.estado())); // Convertir String a Enum
        }

        // Guardar cambios
        return TurnoMapper.toDTO(turnoRepository.save(turno));
    }


    public TurnoDTO buscarPorId(Long id) {
        Turno turno = turnoRepository.findById(id)
                .orElseThrow(() -> new TurnoNotFoundException(id));

        return TurnoMapper.toDTO(turno);
    }

    public List<TurnoDTO> agendaDelProfesional(Long profesionalId, LocalDate fecha, LocalTime hora) {
        if (hora != null) {
            // Filtra por fecha y hora
            return turnoRepository.findByProfesionalIdAndFechaAndHora(profesionalId, fecha, hora)
                    .stream()
                    .map(TurnoMapper::toDTO)
                    .collect(Collectors.toList());
        } else {
            // Si no se pasa hora, devuelve todos los turnos del día
            return turnoRepository.findByProfesionalIdAndFecha(profesionalId, fecha)
                    .stream()
                    .map(TurnoMapper::toDTO)
                    .collect(Collectors.toList());
        }
    }



    public TurnoDTO cancelarTurno(Long id) {
        Turno turno = turnoRepository.findById(id)
                .orElseThrow(() -> new TurnoNotFoundException(id));

        if (turno.getEstado() != EstadoTurno.PENDIENTE) {
            throw new IllegalStateException(
                    "No se puede cancelar un turno que no está pendiente"
            );
        }

        turno.setEstado(EstadoTurno.CANCELADO);
        turnoRepository.save(turno);

        return TurnoMapper.toDTO(turno);
    }

    public List<TurnoDTO> agendaDelCliente(Long clienteId, LocalDate fecha, EstadoTurno estado) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ClientenotFoundException(clienteId));

        List<Turno> turnos;
        if (fecha != null && estado != null) {
            turnos = turnoRepository.findByClienteIdAndFechaAndEstado(clienteId, fecha, estado);
        } else if (fecha != null) {
            turnos = turnoRepository.findByClienteIdAndFecha(clienteId, fecha);
        } else if (estado != null) {
            turnos = turnoRepository.findByClienteIdAndEstado(clienteId, estado);
        } else {
            turnos = turnoRepository.findByClienteId(clienteId);
        }

        return turnos.stream().map(TurnoMapper::toDTO).collect(Collectors.toList());
    }



}


