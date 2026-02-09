package com.elian.Sitema_backend_turnos.controller;

import com.elian.Sitema_backend_turnos.dto.TurnoDTO;
import com.elian.Sitema_backend_turnos.service.TurnoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/pro")
public class ProController {

    private final TurnoService turnoService;

    public ProController(TurnoService turnoService) {
        this.turnoService = turnoService;
    }
}

