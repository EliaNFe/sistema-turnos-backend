package com.elian.Sitema_backend_turnos.controller;

import com.elian.Sitema_backend_turnos.dto.ActualizarTurnoDTO;
import com.elian.Sitema_backend_turnos.dto.CrearTurnoDTO;
import com.elian.Sitema_backend_turnos.dto.TurnoDTO;
import com.elian.Sitema_backend_turnos.mapper.TurnoMapper;
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
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final TurnoService turnoService;
    private final ProfesionalService profesionalService;

    public AdminController(TurnoService turnoService, ProfesionalService profesionalService) {
        this.turnoService = turnoService;
        this.profesionalService = profesionalService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        model.addAttribute(
                "turnos",
                turnoService.listarTodos()
        );

        return "admin-dashboard";
    }

    @GetMapping("/turnos/nuevo")
    public String mostrarFormularioTurno(Model model) {
        model.addAttribute("turno", new CrearTurnoDTO(null, null, null, null));
        return "turno-form";
    }


    @PostMapping("/turnos/nuevo")
    public String crearTurno(@ModelAttribute CrearTurnoDTO dto) {

        turnoService.crearTurno(dto);

        return "redirect:/admin/dashboard";
    }

    @GetMapping("/turnos")
    public String listarTurnos(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String profesionalId,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha,
            Model model) {

        List<TurnoDTO> turnos;

        // limpiar valores vacíos
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

        model.addAttribute("profesionales",
                profesionalService.listarProfesionales());


        return "admin-turnos";
    }




    @GetMapping("/turnos/editar/{id}")
    public String editarTurno(@PathVariable Long id, Model model) {

        TurnoDTO turno = turnoService.buscarPorId(id);

        ActualizarTurnoDTO dto = TurnoMapper.toActualizarDTO(turno);

        model.addAttribute("turno", dto);

        return "admin-turno-editar";
    }


    @PostMapping("/turnos/editar")
    public String guardarEdicion(@ModelAttribute ActualizarTurnoDTO dto,
                                 RedirectAttributes redirect) {
        try {
            turnoService.actualizarTurno(dto.id(), dto);
            redirect.addFlashAttribute("success", "Turno actualizado correctamente");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/turnos";
    }



}

