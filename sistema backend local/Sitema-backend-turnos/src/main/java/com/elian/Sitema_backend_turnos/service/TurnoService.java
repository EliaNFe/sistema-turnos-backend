package com.elian.Sitema_backend_turnos.service;

import com.elian.Sitema_backend_turnos.dto.ActualizarTurnoDTO;
import com.elian.Sitema_backend_turnos.dto.CrearTurnoDTO;
import com.elian.Sitema_backend_turnos.dto.TurnoDTO;
import com.elian.Sitema_backend_turnos.exception.ClientenotFoundException;
import com.elian.Sitema_backend_turnos.exception.ProfesionalNotFoundException;
import com.elian.Sitema_backend_turnos.exception.TurnoNotFoundException;
import com.elian.Sitema_backend_turnos.mapper.TurnoMapper;
import com.elian.Sitema_backend_turnos.model.*;
import com.elian.Sitema_backend_turnos.repository.ClienteRepository;
import com.elian.Sitema_backend_turnos.repository.ProfesionalRepository;
import com.elian.Sitema_backend_turnos.repository.TurnoRepository;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final SecurityService securityService;

    public TurnoService(
            TurnoRepository turnoRepository,
            ClienteRepository clienteRepository,
            ProfesionalRepository profesionalRepository, SecurityService securityService
    ) {
        this.turnoRepository = turnoRepository;
        this.clienteRepository = clienteRepository;
        this.profesionalRepository = profesionalRepository;
        this.securityService = securityService;
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public TurnoDTO crearTurno(CrearTurnoDTO dto) {

        LocalDate hoy = LocalDate.now();
        LocalTime ahora = LocalTime.now();

        if (dto.fecha().isBefore(hoy) ||
                (dto.fecha().isEqual(hoy) && dto.hora().isBefore(ahora))) {
            throw new IllegalStateException("No se pueden crear turnos en el pasado");
        }

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


    public List<TurnoDTO> listarTodos() {
        return turnoRepository.findAll()
                .stream()
                .map(TurnoMapper::toDTO)
                .toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN','PROFESIONAL')")         //Este es para el profesional que es solo para cambiar estados, la responsabilidad de cambiar todo de un turno se lo delego a admin
    public TurnoDTO actualizarEstado(Long turnoId, EstadoTurno nuevoEstado) {

        Turno turno = turnoRepository.findById(turnoId)
                .orElseThrow(() -> new TurnoNotFoundException(turnoId));

        turno.setEstado(nuevoEstado);

        return TurnoMapper.toDTO(turnoRepository.save(turno));    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public TurnoDTO actualizarTurno(Long turnoId, ActualizarTurnoDTO dto) {

        // Buscar turno existente
        Turno turno = turnoRepository.findById(turnoId)
                .orElseThrow(() -> new TurnoNotFoundException(turnoId));

        // Cambio de profesional (opcional)
        if (dto.profesionalId() != null &&
                !dto.profesionalId().equals(turno.getProfesional().getId())) {

            Profesional profesional = profesionalRepository
                    .findById(dto.profesionalId())
                    .orElseThrow(() ->
                            new ProfesionalNotFoundException(dto.profesionalId()));

            turno.setProfesional(profesional);
        }

        // Tomo valores actuales si no vienen nuevos
        LocalDate nuevaFecha =
                dto.fecha() != null ? dto.fecha() : turno.getFecha();

        LocalTime nuevaHora =
                dto.hora() != null ? dto.hora() : turno.getHora();

        // valida solo si cambian fecha/hora

        if (dto.fecha() != null || dto.hora() != null) {

            LocalDate hoy = LocalDate.now();
            LocalTime ahora = LocalTime.now();

            // Turnos en el pasado
            if (nuevaFecha.isBefore(hoy) ||
                    (nuevaFecha.isEqual(hoy) && nuevaHora.isBefore(ahora))) {

                throw new IllegalArgumentException(
                        "No se puede asignar un turno en el pasado"
                );
            }

            // Conflicto de horarios
            Optional<Turno> conflicto =
                    turnoRepository.findByProfesionalIdAndFechaAndHora(
                            turno.getProfesional().getId(),
                            nuevaFecha,
                            nuevaHora
                    );

            if (conflicto.isPresent() &&
                    !conflicto.get().getId().equals(turnoId)) {

                throw new IllegalStateException(
                        "El profesional ya tiene un turno en ese horario"
                );
            }
        }

        // Actualizo solo lo que vino en parametro

        if (dto.fecha() != null)
            turno.setFecha(dto.fecha());

        if (dto.hora() != null)
            turno.setHora(dto.hora());

        if (dto.estado() != null)
            turno.setEstado(EstadoTurno.valueOf(dto.estado()));

        // Guardar
        return TurnoMapper.toDTO(turnoRepository.save(turno));
    }



    public List<TurnoDTO> buscarPorEstado(String estado) {
        EstadoTurno e = EstadoTurno.valueOf(estado);
        return turnoRepository.findByEstado(e)
                .stream()
                .map(TurnoMapper::toDTO)
                .toList();
    }

    public List<TurnoDTO> buscarPorProfesional(Long profesionalId) {
        return turnoRepository.findByProfesionalId(profesionalId)
                .stream()
                .map(TurnoMapper::toDTO)
                .toList();
    }

    public List<TurnoDTO> buscarPorFecha(LocalDate fecha) {
        return turnoRepository.findByFecha(fecha)
                .stream()
                .map(TurnoMapper::toDTO)
                .toList();
    }


    @PreAuthorize("hasAnyRole('ADMIN','PROFESIONAL')")
    public TurnoDTO buscarPorId(Long id) {
        Turno turno = turnoRepository.findById(id)
                .orElseThrow(() -> new TurnoNotFoundException(id));

        return TurnoMapper.toDTO(turno);
    }

    @PreAuthorize("hasAnyRole('ADMIN','PROFESIONAL')")
    public List<TurnoDTO> agendaDelProfesionalLogueado(
            LocalDate fecha) {

        Usuario usuario = securityService.usuarioLogueado();

        if (usuario.getRol() != Rol.PROFESIONAL)
            throw new RuntimeException("No Autorizado");

        Long profesionalId =
                usuario.getProfesional().getId();

        return agendaDelProfesional(profesionalId, fecha, null);
    }

    @PreAuthorize("hasAnyRole('ADMIN','PROFESIONAL')")
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


    @PreAuthorize("hasRole('ADMIN')")
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

    public Page<TurnoDTO> listarFiltradoYPaginado(
            String estado,
            Long profesionalId,
            LocalDate fecha,
            int pagina) {

        PageRequest pageable = PageRequest.of(pagina, 10);

        EstadoTurno enumEstado =
                estado != null ? EstadoTurno.valueOf(estado) : null;

        return turnoRepository.buscarConFiltros(
                enumEstado,
                profesionalId,
                fecha,
                pageable
        ).map(TurnoMapper::toDTO);
    }



    @PreAuthorize("hasAnyRole('ADMIN','PROFESIONAL')")
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


