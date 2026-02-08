package com.elian.Sitema_backend_turnos.controller;

import com.elian.Sitema_backend_turnos.dto.ActualizarEstadoDTO;
import com.elian.Sitema_backend_turnos.dto.ActualizarTurnoDTO;
import com.elian.Sitema_backend_turnos.dto.CrearTurnoDTO;
import com.elian.Sitema_backend_turnos.dto.TurnoDTO;
import com.elian.Sitema_backend_turnos.model.EstadoTurno;
import com.elian.Sitema_backend_turnos.service.TurnoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.cert.TrustAnchor;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/turnos")
public class TurnoController {

    private final TurnoService turnoService;

    public TurnoController(TurnoService turnoService) {
        this.turnoService = turnoService;
    }

    //  Crear turno
    @PostMapping
    public ResponseEntity<TurnoDTO> crear(
            @Valid @RequestBody CrearTurnoDTO dto) {

        return ResponseEntity.ok(
                turnoService.crearTurno(dto)
        );
    }

    //  Buscar por id
    @GetMapping("/{id}")
    public ResponseEntity<TurnoDTO> buscar(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                turnoService.buscarPorId(id)
        );
    }

    //  Agenda profesional
    @GetMapping("/profesional/{id}")
    public ResponseEntity<List<TurnoDTO>> agendaProfesional(
            @PathVariable Long id,
            @RequestParam LocalDate fecha,
            @RequestParam(required = false) LocalTime hora
    ) {

        return ResponseEntity.ok(
                turnoService.agendaDelProfesional(id, fecha, hora)
        );
    }

    //  Agenda cliente
    @GetMapping("/cliente/{id}")
    public ResponseEntity<List<TurnoDTO>> agendaCliente(
            @PathVariable Long id,
            @RequestParam(required = false) LocalDate fecha,
            @RequestParam(required = false) EstadoTurno estado
    ) {

        return ResponseEntity.ok(
                turnoService.agendaDelCliente(id, fecha, estado)
        );
    }

    @GetMapping("/mis-turnos")          //para el usuario que tiene como parametro un profesional, asi ese profesional puede ver sus turnos
    public List<TurnoDTO> misTurnos(
            @RequestParam LocalDate fecha) {

        return turnoService
                .agendaDelProfesionalLogueado(fecha);
    }


    //  Actualizar turno completo (admin)
    @PutMapping("/{id}")
    public ResponseEntity<TurnoDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarTurnoDTO dto
    ) {

        return ResponseEntity.ok(
                turnoService.actualizarTurno(id, dto)
        );
    }

    //  Actualizar estado (profesional)
    @PatchMapping("/{id}/estado")
    public ResponseEntity<TurnoDTO> actualizarEstado(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarEstadoDTO dto
    ) {

        return ResponseEntity.ok(
                turnoService.actualizarEstado(id, dto.getEstado())
        );
    }

    //  Cancelar turno
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelar(
            @PathVariable Long id) {

        turnoService.cancelarTurno(id);
        return ResponseEntity.noContent().build();
    }
}


