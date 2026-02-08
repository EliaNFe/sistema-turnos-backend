package com.elian.Sitema_backend_turnos.service;

import com.elian.Sitema_backend_turnos.dto.CrearUsuarioDTO;
import com.elian.Sitema_backend_turnos.exception.ProfesionalNotFoundException;
import com.elian.Sitema_backend_turnos.model.Profesional;
import com.elian.Sitema_backend_turnos.model.Rol;
import com.elian.Sitema_backend_turnos.model.Usuario;
import com.elian.Sitema_backend_turnos.repository.ProfesionalRepository;
import com.elian.Sitema_backend_turnos.repository.UsuarioRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ProfesionalRepository profesionalRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            ProfesionalRepository profesionalRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.profesionalRepository = profesionalRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Usuario crearUsuario(CrearUsuarioDTO dto) {

        Usuario usuario = new Usuario();

        usuario.setUsername(dto.username());
        usuario.setPassword(
                passwordEncoder.encode(dto.password())
        );

        usuario.setRol(
                Rol.valueOf(String.valueOf(dto.rol()))
        );

        if (dto.profesionalId() != null) {

            Profesional profesional =
                    profesionalRepository.findById(dto.profesionalId())
                            .orElseThrow(() ->
                                    new ProfesionalNotFoundException(dto.profesionalId()));

            usuario.setProfesional(profesional);
        }

        return usuarioRepository.save(usuario);
    }
}


