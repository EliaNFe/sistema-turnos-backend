package com.elian.Sitema_backend_turnos.controller;

import com.elian.Sitema_backend_turnos.dto.ActualizarTurnoDTO;
import com.elian.Sitema_backend_turnos.dto.CrearTurnoDTO;
import com.elian.Sitema_backend_turnos.dto.TurnoDTO;
import com.elian.Sitema_backend_turnos.model.EstadoTurno;
import com.elian.Sitema_backend_turnos.service.TurnoService;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public TurnoDTO crear(@RequestBody CrearTurnoDTO dto) {
        return turnoService.crearTurno(dto);
    }

    @GetMapping("/{id}")
    public TurnoDTO buscar(@PathVariable Long id) {
        return turnoService.buscarPorId(id);
    }

    @GetMapping("/profesional/{id}")
    public List<TurnoDTO> agenda(
            @PathVariable Long id,
            @RequestParam LocalDate fecha,
            @RequestParam(required = false) LocalTime hora,
            @RequestParam(required = false) EstadoTurno estado
    ) {
        return turnoService.agendaDelProfesional(id, fecha, hora);
    }

    @PutMapping("/{id}")
    public TurnoDTO actualizar(
            @PathVariable Long id,
            @RequestBody ActualizarTurnoDTO dto
    ) {
        return turnoService.actualizarTurno(id, dto);
    }

    @GetMapping("/cliente/{id}")
    public List<TurnoDTO> agendaCliente(
            @PathVariable Long id,
            @RequestParam(required = false) LocalDate fecha,
            @RequestParam(required = false) EstadoTurno estado
    ) {
        return turnoService.agendaDelCliente(id, fecha, estado);
    }



    @PutMapping("/{id}/cancelar")
    public void cancelar(@PathVariable Long id) {
        turnoService.cancelarTurno(id);
    }
}

