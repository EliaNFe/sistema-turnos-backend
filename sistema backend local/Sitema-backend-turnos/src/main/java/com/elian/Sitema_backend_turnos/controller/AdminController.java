package com.elian.Sitema_backend_turnos.controller;
import com.elian.Sitema_backend_turnos.dto.*;
import com.elian.Sitema_backend_turnos.service.TurnoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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

        long turnosHoy = todos.stream().filter(t -> t.fecha().equals(hoy)).count();
        long pendientes = todos.stream().filter(t -> t.estado().toString().equals("PENDIENTE")).count();

        model.addAttribute("cantidadHoy", turnosHoy);
        model.addAttribute("cantidadPendientes", pendientes);
        model.addAttribute("turnos", todos);

        return "admin-dashboard";
    }
}