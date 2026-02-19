package com.elian.Sitema_backend_turnos.service;

import com.elian.Sitema_backend_turnos.dto.ActualizarTurnoDTO;
import com.elian.Sitema_backend_turnos.dto.CrearTurnoDTO;
import com.elian.Sitema_backend_turnos.dto.CrearTurnoWebDTO;
import com.elian.Sitema_backend_turnos.dto.TurnoDTO;
import com.elian.Sitema_backend_turnos.exception.ClientenotFoundException;
import com.elian.Sitema_backend_turnos.exception.ProfesionalNotFoundException;
import com.elian.Sitema_backend_turnos.exception.TurnoNotFoundException;
import com.elian.Sitema_backend_turnos.mapper.TurnoMapper;
import com.elian.Sitema_backend_turnos.model.*;
import com.elian.Sitema_backend_turnos.repository.ClienteRepository;
import com.elian.Sitema_backend_turnos.repository.ProfesionalRepository;
import com.elian.Sitema_backend_turnos.repository.TurnoRepository;
import com.elian.Sitema_backend_turnos.repository.UsuarioRepository;
import org.springframework.cglib.core.Local;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TurnoService {

    private final TurnoRepository turnoRepository;
    private final ClienteRepository clienteRepository;
    private final ProfesionalRepository profesionalRepository;
    private final SecurityService securityService;
    private final TurnoMapper turnoMapper;
    private final UsuarioRepository usuarioRepository;
    private final ProfesionalService profesionalService;

    public TurnoService(
            TurnoRepository turnoRepository,
            ClienteRepository clienteRepository,
            ProfesionalRepository profesionalRepository, SecurityService securityService, TurnoMapper turnoMapper, UsuarioRepository usuarioRepository, ProfesionalService profesionalService
    ) {
        this.turnoRepository = turnoRepository;
        this.clienteRepository = clienteRepository;
        this.profesionalRepository = profesionalRepository;
        this.securityService = securityService;
        this.turnoMapper = turnoMapper;
        this.usuarioRepository = usuarioRepository;
        this.profesionalService = profesionalService;
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public TurnoDTO crearTurno(CrearTurnoDTO dto) {

        LocalDate hoy = LocalDate.now();
        LocalTime ahora = LocalTime.now();


        if (dto.fecha() == null || dto.hora() == null) {
            throw new IllegalArgumentException("La fecha y la hora son obligatorias");
        }

        if (dto.fecha().isBefore(hoy) ||
                (dto.fecha().isEqual(hoy) && dto.hora().isBefore(ahora))) {
            throw new IllegalArgumentException("No se pueden crear turnos en el pasado");
        }

        if (dto.clienteId() == null || dto.profesionalId() == null) {
            throw new IllegalArgumentException("Debe seleccionar cliente y profesional");
        }

        if (dto.hora().isBefore(LocalTime.of(8,0)) || dto.hora().isAfter(LocalTime.of(20,0))) {
            throw new IllegalStateException("El turno debe estar dentro del horario laboral");
        }

        Profesional profesional = profesionalRepository.findById(dto.profesionalId())
                .orElseThrow(() -> new ProfesionalNotFoundException(dto.profesionalId()));

        Cliente cliente = clienteRepository.findById(dto.clienteId())
                .orElseThrow(() -> new ClientenotFoundException(dto.clienteId()));

        if (!profesional.isActivo()) {
            throw new IllegalStateException("El profesional seleccionado no está activo.");
        }


        if (!cliente.isActivo()) {
            throw new IllegalStateException("El cliente seleccionado no está activo.");
        }

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
        if (dto.profesionalId() != null && !dto.profesionalId().equals(turno.getProfesional().getId())) {
            Profesional nuevoProfesional = profesionalRepository.findById(dto.profesionalId())
                    .orElseThrow(() -> new ProfesionalNotFoundException(dto.profesionalId()));

            // Validación de Baja Lógica
            if (!nuevoProfesional.isActivo()) {
                throw new IllegalStateException("No se puede asignar el turno a un profesional inactivo.");
            }

            turno.setProfesional(nuevoProfesional);
        }

        // Validar solo si cambian fecha/hora
        if ((dto.fecha() != null && !dto.fecha().equals(turno.getFecha())) ||
                (dto.hora() != null && !dto.hora().equals(turno.getHora()))) {


            LocalDate nuevaFecha = dto.fecha() != null ? dto.fecha() : turno.getFecha();
            LocalTime nuevaHora = dto.hora() != null ? dto.hora() : turno.getHora();

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

            if (conflicto.isPresent() && !conflicto.get().getId().equals(turnoId)) {
                throw new IllegalStateException(
                        "El profesional ya tiene un turno en ese horario"
                );
            }

            // Actualizo fecha/hora si vinieron
            if (dto.fecha() != null) turno.setFecha(dto.fecha());
            if (dto.hora() != null) turno.setHora(dto.hora());
        }

        // Estado siempre se puede actualizar
        if (dto.estado() != null) {
            turno.setEstado(EstadoTurno.valueOf(dto.estado()));
        }

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

    public List<LocalTime> listarHorariosDisponiblesGlobal(LocalDate fecha) {

        List<LocalTime> disponibles = new ArrayList<>();

        LocalTime inicio = LocalTime.of(8, 0);
        LocalTime fin = LocalTime.of(20, 0);

        List<Profesional> activos = profesionalRepository.findAll()
                .stream()
                .filter(Profesional::isActivo)
                .toList();

        int cantidadProfesionales = activos.size();

        if (cantidadProfesionales == 0) {
            return List.of();
        }

        List<EstadoTurno> estadosQueBloquean = List.of(
                EstadoTurno.PENDIENTE,
                EstadoTurno.CONFIRMADO
        );

        List<Turno> turnosDelDia =
                turnoRepository.findByFechaAndEstadoIn(fecha, estadosQueBloquean);


        while (inicio.isBefore(fin)) {

            final LocalTime horaActual = inicio;

            long ocupados = turnosDelDia.stream()
                    .filter(t -> t.getHora().equals(horaActual))
                    .count();

            if (ocupados < cantidadProfesionales) {
                disponibles.add(horaActual);
            }

            inicio = inicio.plusMinutes(30);
        }

        return disponibles;
    }

    @Transactional
    public void registrarSolicitudWeb(CrearTurnoWebDTO dto) {
        // Si el usuario marco inexistnte pero el DNI ya está...
        Optional<Cliente> existente = clienteRepository.findByDocumento(dto.documento());

        // Si mando nombre que es nuevo pero el DNI ya existe:
        if (dto.nombre() != null && !dto.nombre().isBlank() && existente.isPresent()) {
            throw new IllegalArgumentException("Ya figurás en nuestro sistema. Por favor, seleccioná 'Ya soy cliente' e ingresá solo tu DNI.");
        }
        LocalDate hoy = LocalDate.now();
        LocalTime ahora = LocalTime.now();

        if (dto.fecha() == null || dto.hora() == null) {
            throw new IllegalArgumentException("Fecha y hora son obligatorias");
        }

        if (dto.fecha().isBefore(hoy) ||
                (dto.fecha().isEqual(hoy) && dto.hora().isBefore(ahora))) {
            throw new IllegalArgumentException("No se puede solicitar un turno en el pasado");
        }

        if (dto.hora().isBefore(LocalTime.of(8, 0)) ||
                dto.hora().isAfter(LocalTime.of(20, 0))) {
            throw new IllegalStateException("Horario fuera del rango laboral");
        }

        Cliente cliente = clienteRepository.findByDocumento(dto.documento())
                .orElseGet(() -> {
                    Cliente nuevo = new Cliente();
                    nuevo.setNombre(dto.nombre());
                    nuevo.setApellido(dto.apellido());
                    nuevo.setDocumento(dto.documento());
                    nuevo.setTelefono(dto.telefono());
                    nuevo.setEmail(dto.email());
                    nuevo.setActivo(true);
                    return clienteRepository.save(nuevo);
                });

        Profesional profesionalDisponible =
                profesionalService.buscarDisponible(dto.fecha(), dto.hora());

        Turno turno = new Turno();
        turno.setCliente(cliente);
        turno.setProfesional(profesionalDisponible);
        turno.setFecha(dto.fecha());
        turno.setHora(dto.hora());
        turno.setEstado(EstadoTurno.SOLICITADO);

        try {
            turnoRepository.save(turno);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("El horario acaba de ser tomado. Intente nuevamente.");
        }
    }


    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public TurnoDTO confirmarSolicitud(Long turnoId) {

        Turno turno = turnoRepository.findById(turnoId)
                .orElseThrow(() -> new TurnoNotFoundException(turnoId));

        if (turno.getEstado() != EstadoTurno.SOLICITADO) {
            throw new IllegalStateException("El turno no está en estado SOLICITADO");
        }

        List<Profesional> activos = profesionalRepository.findAll()
                .stream()
                .filter(Profesional::isActivo)
                .toList();

        for (Profesional p : activos) {

            boolean ocupado = turnoRepository
                    .findByProfesionalIdAndFechaAndHora(
                            p.getId(),
                            turno.getFecha(),
                            turno.getHora()
                    ).isPresent();

            if (!ocupado) {
                turno.setProfesional(p);
                turno.setEstado(EstadoTurno.PENDIENTE);
                return turnoMapper.toDTO(turnoRepository.save(turno));
            }
        }

        throw new IllegalStateException("No hay profesionales disponibles");
    }



    @PreAuthorize("hasRole('ADMIN')")
    public TurnoDTO cancelarTurno(Long id) {
        Turno turno = turnoRepository.findById(id)
                .orElseThrow(() -> new TurnoNotFoundException(id));

        if (turno.getEstado() != EstadoTurno.PENDIENTE && turno.getEstado() != EstadoTurno.CONFIRMADO && turno.getEstado() != EstadoTurno.SOLICITADO) {
            throw new IllegalStateException(
                    "No se puede cancelar un turno que no está pendiente, confirmado o solicitado"
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


    public List<TurnoDTO> listarTurnosPorEmailProfesionalYFecha(String username, LocalDate fecha) {
        //  Buscamos al usuario por su username para obtener su Profesional vinculado
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getProfesional() == null) {
            throw new RuntimeException("El usuario no tiene un perfil profesional vinculado");
        }

        // tenemos el ID del profesional, buscamos sus turnos
        Long profesionalId = usuario.getProfesional().getId();

        return turnoRepository.findByProfesionalIdAndFechaOrderByHoraAsc(profesionalId, fecha)
                .stream()
                .map(turnoMapper::toDTO2)
                .collect(Collectors.toList());
    }

    public List<TurnoDTO> listarHistorialCliente(String username, String clienteNombre) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Long profesionalId = usuario.getProfesional().getId();

        // Buscamos todos los turnos de ese cliente con ese profesional, ordenados por fecha
        return turnoRepository.findByProfesionalIdAndClienteNombreOrderByFechaDesc(profesionalId, clienteNombre)
                .stream()
                .map(turnoMapper::toDTO2)
                .collect(Collectors.toList());
    }

    public List<TurnoDTO> listarPorProfesionalYFecha(String username, LocalDate fecha) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getProfesional() == null) {
            throw new RuntimeException("El usuario no tiene un perfil profesional vinculado");
        }

        return turnoRepository.findByProfesionalIdAndFechaOrderByHoraAsc(usuario.getProfesional().getId(), fecha)
                .stream()
                .map(turnoMapper::toDTO2) // Usamos toDTO2 como en tus otros métodos
                .collect(Collectors.toList());
    }


    public List<TurnoDTO> listarTodoDelProfesional(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getProfesional() == null) {
            throw new RuntimeException("El usuario no tiene un perfil profesional vinculado");
        }

        // Buscamos todos los turnos del profesional ordenados por fecha descendente (lo más nuevo primero)
        return turnoRepository.findByProfesionalIdOrderByFechaDescHoraAsc(usuario.getProfesional().getId())
                .stream()
                .map(turnoMapper::toDTO2)
                .collect(Collectors.toList());
    }

    @Transactional
    public void completarTurno(Long id) {
        Turno turno = turnoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

        turno.setEstado(EstadoTurno.COMPLETADO);
        // Al estar en un método @Transactional, se guarda solo al terminar
    }

}


