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

    public boolean tieneUsuarioAsociado(Long profesionalId) {
        if (profesionalId == null) return false;
        return usuarioRepository.existsByProfesionalId(profesionalId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Usuario crearUsuario(CrearUsuarioDTO dto) {
        //  Validación de integridad de negocio
        if (Rol.PROFESIONAL.equals(dto.rol()) && dto.profesionalId() == null) {
            throw new IllegalArgumentException("Un usuario con rol PROFESIONAL debe tener un Profesional asignado.");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(dto.username());
        usuario.setPassword(passwordEncoder.encode(dto.password()));
        usuario.setRol(dto.rol());

        if (dto.profesionalId() != null) {
            Profesional profesional = profesionalRepository.findById(dto.profesionalId())
                    .orElseThrow(() -> new ProfesionalNotFoundException(dto.profesionalId()));
            usuario.setProfesional(profesional);
        }

        return usuarioRepository.save(usuario);
    }
}


