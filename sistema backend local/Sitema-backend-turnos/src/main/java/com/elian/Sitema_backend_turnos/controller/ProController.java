package com.elian.Sitema_backend_turnos.controller;

import com.elian.Sitema_backend_turnos.dto.TurnoDTO;
import com.elian.Sitema_backend_turnos.service.TurnoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

        return "profesional/dashboard";
    }
}
