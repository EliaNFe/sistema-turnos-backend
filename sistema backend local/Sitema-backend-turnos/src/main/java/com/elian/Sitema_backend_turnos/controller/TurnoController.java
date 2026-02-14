package com.elian.Sitema_backend_turnos.controller;

import com.elian.Sitema_backend_turnos.dto.ActualizarTurnoDTO;
import com.elian.Sitema_backend_turnos.dto.CrearTurnoDTO;
import com.elian.Sitema_backend_turnos.dto.TurnoDTO;
import com.elian.Sitema_backend_turnos.mapper.TurnoMapper;
import com.elian.Sitema_backend_turnos.service.ClienteService;
import com.elian.Sitema_backend_turnos.service.ProfesionalService;
import com.elian.Sitema_backend_turnos.service.TurnoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/turnos")
@PreAuthorize("hasRole('ADMIN')")
public class TurnoController {

    private final TurnoService turnoService;
    private final ProfesionalService profesionalService;
    private final ClienteService clienteService;

    public TurnoController(TurnoService turnoService, ProfesionalService profesionalService, ClienteService clienteService) {
        this.turnoService = turnoService;
        this.profesionalService = profesionalService;
        this.clienteService = clienteService;
    }

    // Listado de turnos con filtros
    @GetMapping
    public String listarTurnos(@RequestParam(required = false) String estado,
                               @RequestParam(required = false) String profesionalId,
                               @RequestParam(required = false)
                               @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha,
                               Model model) {
        List<TurnoDTO> turnos;

        // Limpieza de parámetros vacíos
        if (estado != null && estado.isBlank()) estado = null;
        if (profesionalId != null && profesionalId.isBlank()) profesionalId = null;

        if (estado != null) {
            turnos = turnoService.buscarPorEstado(estado);
        } else if (profesionalId != null) {
            turnos = turnoService.buscarPorProfesional(Long.parseLong(profesionalId));
        } else if (fecha != null) {
            turnos = turnoService.buscarPorFecha(fecha);
        } else {
            turnos = turnoService.listarTodos();
        }

        model.addAttribute("turnos", turnos);
        model.addAttribute("profesionales", profesionalService.listarProfesionales());
        return "admin-turnos";
    }

    // Formulario para crear un nuevo turno (Carga clientes y profesionales activos)
    @GetMapping("/nuevo")
    public String nuevoTurno(Model model) {
        model.addAttribute("turno", new CrearTurnoDTO(null, null, null, null));
        model.addAttribute("clientes", clienteService.listarClientesActivosSinPaginacion());
        model.addAttribute("profesionales", profesionalService.listarActivos());
        return "turno-form";
    }

    @PostMapping("/nuevo")
    public String crearTurno(@ModelAttribute CrearTurnoDTO dto, RedirectAttributes redirect) {
        turnoService.crearTurno(dto);
        redirect.addFlashAttribute("success", "Turno creado correctamente");
        return "redirect:/admin/dashboard"; // O a /admin/turnos según prefieras
    }

    // Edición de turnos
    @GetMapping("/editar/{id}")
    public String editarTurno(@PathVariable Long id, Model model) {
        TurnoDTO turno = turnoService.buscarPorId(id);
        ActualizarTurnoDTO dto = TurnoMapper.toActualizarDTO(turno);
        model.addAttribute("turno", dto);
        return "admin-turno-editar";
    }

    @PostMapping("/editar")
    public String guardarEdicion(@ModelAttribute ActualizarTurnoDTO dto,
                                 RedirectAttributes redirect) {
        turnoService.actualizarTurno(dto.id(), dto);
        redirect.addFlashAttribute("success", "Turno actualizado correctamente");
        return "redirect:/admin/turnos";
    }

    // Cancelación (Baja)
    @PostMapping("/cancelar/{id}")
    public String cancelarTurno(@PathVariable Long id, RedirectAttributes redirect) {
        turnoService.cancelarTurno(id);
        redirect.addFlashAttribute("success", "Turno cancelado correctamente");
        return "redirect:/admin/turnos";
    }
}