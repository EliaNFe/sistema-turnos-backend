package com.elian.Sitema_backend_turnos.service;

import com.elian.Sitema_backend_turnos.dto.ActualizarTurnoDTO;
import com.elian.Sitema_backend_turnos.dto.CrearTurnoDTO;
import com.elian.Sitema_backend_turnos.dto.TurnoDTO;
import com.elian.Sitema_backend_turnos.exception.ClientenotFoundException;
import com.elian.Sitema_backend_turnos.exception.ProfesionalNotFoundException;
import com.elian.Sitema_backend_turnos.exception.TurnoNotFoundException;
import com.elian.Sitema_backend_turnos.model.Cliente;
import com.elian.Sitema_backend_turnos.model.EstadoTurno;
import com.elian.Sitema_backend_turnos.model.Profesional;
import com.elian.Sitema_backend_turnos.model.Turno;
import com.elian.Sitema_backend_turnos.repository.ClienteRepository;
import com.elian.Sitema_backend_turnos.repository.ProfesionalRepository;
import com.elian.Sitema_backend_turnos.repository.TurnoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@WithMockUser(roles = "ADMIN")
class TurnoServiceTest {

    @Autowired
    private TurnoService turnoService;

    @Autowired
    private TurnoRepository turnoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProfesionalRepository profesionalRepository;

    private Long clienteId;
    private Long profesionalId;

    @BeforeEach
    void setup() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Juan");
        cliente.setApellido("bananashe");
        cliente.setDocumento("8888888");
        cliente.setTelefono("3811234567");
        cliente.setEmail("juan@test.com");

        cliente = clienteRepository.save(cliente);
        clienteId = cliente.getId();

        Profesional profesional = new Profesional();
        profesional.setNombre("Ana");
        profesional.setEspecialidad("Anashe bananashe");

        profesional = profesionalRepository.save(profesional);
        profesionalId = profesional.getId();
    }



    @Test
    void testCrearTurno() {
        CrearTurnoDTO dto = new CrearTurnoDTO(
                clienteId,
                profesionalId,
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0)
               // LocalDate.of(2024, 2, 28),        // para probar que no se puede agregar en fechas pasadas
                //LocalTime.of(10, 0)
        );

        TurnoDTO creado = turnoService.crearTurno(dto);

        assertNotNull(creado);
        assertEquals(EstadoTurno.PENDIENTE, creado.estado());
        assertEquals(clienteId, creado.clienteId());
        assertEquals(profesionalId, creado.profesionalId());
    }

    @Test
    void testActualizarTurno() {
        // Primero creamos un turno
        CrearTurnoDTO crearDto = new CrearTurnoDTO(
                clienteId,
                profesionalId,
                LocalDate.now().plusDays(1),
                LocalTime.of(11, 0)
        );
        TurnoDTO turno = turnoService.crearTurno(crearDto);

        ActualizarTurnoDTO actualizarDto = new ActualizarTurnoDTO(
                null, // No cambiamos profesional
                LocalDate.now().plusDays(2),
                LocalTime.of(12, 0),
                "CANCELADO"
        );

        TurnoDTO actualizado = turnoService.actualizarTurno(turno.id(), actualizarDto);

        assertEquals(LocalDate.now().plusDays(2), actualizado.fecha());
        assertEquals(LocalTime.of(12, 0), actualizado.hora());
        assertEquals(EstadoTurno.CANCELADO, actualizado.estado());
    }

    @Test
    void testBuscarPorId() {
        CrearTurnoDTO dto = new CrearTurnoDTO(
                clienteId,
                profesionalId,
                LocalDate.now().plusDays(3),
                LocalTime.of(14, 0)
        );
        TurnoDTO creado = turnoService.crearTurno(dto);

        TurnoDTO encontrado = turnoService.buscarPorId(creado.id());
        assertEquals(creado.id(), encontrado.id());
        assertEquals(creado.clienteId(), encontrado.clienteId());
    }

    @Test
    void buscarTurnoInexistente() {
        assertThrows(TurnoNotFoundException.class,
                () -> turnoService.buscarPorId(999L));
    }

    @Test
    void noDebeCrearTurnoDuplicado() {
        CrearTurnoDTO dto = new CrearTurnoDTO(
                clienteId,
                profesionalId,
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0)
        );

        turnoService.crearTurno(dto);

        assertThrows(RuntimeException.class,
                () -> turnoService.crearTurno(dto));
    }

    @Test
    void testCancelarTurno() {
        CrearTurnoDTO dto = new CrearTurnoDTO(
                clienteId,
                profesionalId,
                LocalDate.now().plusDays(1),
                LocalTime.of(15, 0)
        );
        TurnoDTO creado = turnoService.crearTurno(dto);

        TurnoDTO cancelado = turnoService.cancelarTurno(creado.id());
        assertEquals(EstadoTurno.CANCELADO, cancelado.estado());
    }

    @Test
    void testAgendaDelProfesional() {
        CrearTurnoDTO dto1 = new CrearTurnoDTO(clienteId, profesionalId, LocalDate.now().plusDays(1), LocalTime.of(9, 0));
        CrearTurnoDTO dto2 = new CrearTurnoDTO(clienteId, profesionalId, LocalDate.now().plusDays(1), LocalTime.of(10, 0));
        turnoService.crearTurno(dto1);
        turnoService.crearTurno(dto2);

        List<TurnoDTO> agenda = turnoService.agendaDelProfesional(profesionalId, LocalDate.now().plusDays(1), null);
        assertEquals(2, agenda.size());
    }

    @Test
    void testAgendaDelCliente() {
        CrearTurnoDTO dto = new CrearTurnoDTO(clienteId, profesionalId, LocalDate.now().plusDays(1), LocalTime.of(16, 0));
        turnoService.crearTurno(dto);

        List<TurnoDTO> agenda = turnoService.agendaDelCliente(clienteId, LocalDate.now().plusDays(1), null);
        assertFalse(agenda.isEmpty());
        assertEquals(clienteId, agenda.get(0).clienteId());
    }
}
