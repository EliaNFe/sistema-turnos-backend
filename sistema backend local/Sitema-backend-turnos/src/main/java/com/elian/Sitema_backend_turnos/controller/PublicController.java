package com.elian.Sitema_backend_turnos.controller;

import com.elian.Sitema_backend_turnos.dto.CrearTurnoWebDTO;
import com.elian.Sitema_backend_turnos.service.ProfesionalService;
import com.elian.Sitema_backend_turnos.service.TurnoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/reservar")
public class PublicController {

    private final TurnoService turnoService;
    private final ProfesionalService profesionalService;

    public PublicController(TurnoService turnoService, ProfesionalService profesionalService) {
        this.turnoService = turnoService;
        this.profesionalService = profesionalService;
    }

    // Página principal del calendario
    @GetMapping
    public String verCalendario(Model model) {
        return "publico-calendario";
    }

    // Endpoint para que el JS consulte horarios libres de un día
    @GetMapping("/horarios")
    @ResponseBody
    public List<LocalTime> obtenerHorarios(@RequestParam String fecha) {
        return turnoService.listarHorariosDisponiblesGlobal(LocalDate.parse(fecha));
    }

    @PostMapping("/confirmar")
    public String procesarReserva(@ModelAttribute CrearTurnoWebDTO dto,
                                  RedirectAttributes redirect) {

        try {
            turnoService.registrarSolicitudWeb(dto);

            redirect.addFlashAttribute("success",
                    "¡Solicitud enviada! El administrador te confirmará pronto.");

        } catch (Exception e) {

            redirect.addFlashAttribute("error",
                    "Hubo un problema: " + e.getMessage());
        }

        return "redirect:/reservar";
    }


}