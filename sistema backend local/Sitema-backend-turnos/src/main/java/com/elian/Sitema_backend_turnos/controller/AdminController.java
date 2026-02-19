package com.elian.Sitema_backend_turnos.controller;

import com.elian.Sitema_backend_turnos.dto.TurnoDTO;
import com.elian.Sitema_backend_turnos.model.EstadoTurno;
import com.elian.Sitema_backend_turnos.service.TurnoService;
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

    public AdminController(TurnoService turnoService) {
        this.turnoService = turnoService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<TurnoDTO> todos = turnoService.listarTodos();
        LocalDate hoy = LocalDate.now();

        // Estadísticas rápidas
        long turnosHoy = todos.stream().filter(t -> t.fecha().equals(hoy)).count();
        long pendientes = todos.stream().filter(t -> t.estado().name().equals("PENDIENTE")).count();

        // Separamos las solicitudes web para mostrarlas en una sección destacada
        List<TurnoDTO> solicitudesWeb = todos.stream()
                .filter(t -> t.estado().name().equals("SOLICITADO"))
                .toList();

        model.addAttribute("cantidadHoy", turnosHoy);
        model.addAttribute("cantidadPendientes", pendientes);
        model.addAttribute("solicitudes", solicitudesWeb); // Nueva lista para el HTML
        model.addAttribute("turnos", todos);

        return "admin-dashboard";
    }

    @PostMapping("/turnos/confirmar/{id}")
    public String confirmarTurno(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            turnoService.confirmarSolicitud(id);
            redirect.addFlashAttribute("success", "Turno confirmado y asignado correctamente.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error al confirmar: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/turnos/rechazar/{id}")
    public String rechazarTurno(@PathVariable Long id, RedirectAttributes redirect) {
        turnoService.actualizarEstado(id, EstadoTurno.CANCELADO);
        redirect.addFlashAttribute("info", "Solicitud de turno rechazada.");
        return "redirect:/admin/dashboard";
    }
}