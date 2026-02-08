package com.elian.Sitema_backend_turnos.controller;

import com.elian.Sitema_backend_turnos.dto.CrearUsuarioDTO;
import com.elian.Sitema_backend_turnos.model.Usuario;
import com.elian.Sitema_backend_turnos.service.UsuarioService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Usuario crear(@RequestBody CrearUsuarioDTO dto) {
        return usuarioService.crearUsuario(dto);
    }
}

