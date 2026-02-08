package com.elian.Sitema_backend_turnos.controller;

import com.elian.Sitema_backend_turnos.dto.UsuarioLogueadoDTO;
import com.elian.Sitema_backend_turnos.service.SecurityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final SecurityService securityService;

    public AuthController(SecurityService securityService) {
        this.securityService = securityService;
    }

    @GetMapping("/me")
    public UsuarioLogueadoDTO me() {
        return securityService.usuarioActual();
    }
}

