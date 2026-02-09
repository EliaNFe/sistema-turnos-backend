package com.elian.Sitema_backend_turnos.controller;

import com.elian.Sitema_backend_turnos.security.CustomUserDetails;
import com.elian.Sitema_backend_turnos.service.TurnoService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

@Controller         //usamos controller y no restcontroller porque devuelve vistas, anotadoo
public class WebController {

    private final TurnoService turnoService;

    public WebController(TurnoService turnoService) {
        this.turnoService = turnoService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/redirect")
    public String redirect(Authentication auth) {

        CustomUserDetails user =
                (CustomUserDetails) auth.getPrincipal();

        switch (user.getUsuario().getRol()) {

            case ADMIN:
                return "redirect:/admin/dashboard";

            case PROFESIONAL:
                return "redirect:/pro/dashboard";

            default:
                return "redirect:/login";
        }
    }

    @GetMapping("/pro/dashboard")
    public String dashboardProfesional(Model model) {       //esta aca y no en PROCONTROLLER porque webcontroller se encarga de login redirect, dashboard y paginas

        LocalDate hoy = LocalDate.now();

        var turnos =
                turnoService.agendaDelProfesionalLogueado(hoy);

        model.addAttribute("turnos", turnos);
        model.addAttribute("fecha", hoy);

        return "pro-dashboard";
    }
}

