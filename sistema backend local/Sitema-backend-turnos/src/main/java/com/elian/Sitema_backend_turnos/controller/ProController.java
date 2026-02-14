package com.elian.Sitema_backend_turnos.controller;

import com.elian.Sitema_backend_turnos.dto.TurnoDTO;
import com.elian.Sitema_backend_turnos.service.TurnoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/profesional")
public class ProController {

    private final TurnoService turnoService;

    public ProController(TurnoService turnoService) {
        this.turnoService = turnoService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model,
                            @AuthenticationPrincipal UserDetails userDetails,
                            @RequestParam(name = "fecha", required = false)
                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        // Si no mandan fecha, asumimos que es hoy
        LocalDate fechaBusqueda = (fecha == null) ? LocalDate.now() : fecha;

        // El 'username' debería ser el email o identificador del profesional en la BD
        String emailProf = userDetails.getUsername();

        // Buscamos los turnos específicos
        List<TurnoDTO> misTurnos = turnoService.listarTurnosPorEmailProfesionalYFecha(emailProf, fechaBusqueda);

        model.addAttribute("turnos", misTurnos);
        model.addAttribute("fechaSeleccionada", fechaBusqueda);
        model.addAttribute("username", emailProf);

        return "pro-dashboard";
    }

    @GetMapping("/agenda-general")
    public String agendaGeneral(@RequestParam(required = false) String fecha,
                                @AuthenticationPrincipal UserDetails userDetails,
                                Model model) {
        String username = userDetails.getUsername();
        List<TurnoDTO> turnos;

        if (fecha != null && !fecha.isEmpty()) {
            // Parseamos el String de la vista a LocalDate
            LocalDate fechaParsed = LocalDate.parse(fecha);
            turnos = turnoService.listarPorProfesionalYFecha(username, fechaParsed);
        } else {
            turnos = turnoService.listarTodoDelProfesional(username);
        }

        model.addAttribute("turnos", turnos);
        model.addAttribute("fechaSeleccionada", fecha);
        return "profesional-agenda-general";
    }


    @GetMapping("/clientes/historial")
    public String historialCliente(@RequestParam String nombre,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   Model model) {
        String username = userDetails.getUsername();
        // Este método lo creamos en el service
        List<TurnoDTO> historial = turnoService.listarHistorialCliente(username, nombre);

        model.addAttribute("clienteNombre", nombre);
        model.addAttribute("historial", historial);
        return "profesional-historial-cliente";
    }

    @PostMapping("/turnos/completar/{id}")
    public String completar(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            turnoService.completarTurno(id);
            redirect.addFlashAttribute("success", "Turno marcado como completado.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "No se pudo actualizar el turno.");
        }
        return "redirect:/profesional/dashboard";
    }


    @PostMapping("/turnos/cancelar/{id}")
    public String cancelar(@PathVariable Long id, RedirectAttributes redirect) {
        turnoService.cancelarTurno(id);
        redirect.addFlashAttribute("error", "Turno cancelado.");
        return "redirect:/profesional/dashboard";
    }


}
