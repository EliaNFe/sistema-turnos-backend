package com.elian.Sitema_backend_turnos.service;

import com.elian.Sitema_backend_turnos.dto.UsuarioLogueadoDTO;
import com.elian.Sitema_backend_turnos.model.Usuario;
import com.elian.Sitema_backend_turnos.repository.UsuarioRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    private final UsuarioRepository usuarioRepository;

    public SecurityService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioLogueadoDTO usuarioActual() {

        Usuario usuario = usuarioLogueado();

        Long profesionalId = null;

        if (usuario.getProfesional() != null) {
            profesionalId = usuario.getProfesional().getId();
        }

        return new UsuarioLogueadoDTO(
                usuario.getUsername(),
                usuario.getRol().name(),
                profesionalId
        );
    }


    public Usuario usuarioLogueado() {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        String username = auth.getName();

        return usuarioRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("Usuario no encontrado"));
    }
}

