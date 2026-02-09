package com.elian.Sitema_backend_turnos.controller;

import com.elian.Sitema_backend_turnos.service.TurnoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

        model.addAttribute(
                "turnos",
                turnoService.listarTodos()
        );

        return "admin-dashboard";
    }

    @GetMapping("/turnos")
    public String verTurnos(Model model) {

        model.addAttribute(
                "turnos",
                turnoService.listarTodos()
        );

        return "admin-turnos";
    }

}

