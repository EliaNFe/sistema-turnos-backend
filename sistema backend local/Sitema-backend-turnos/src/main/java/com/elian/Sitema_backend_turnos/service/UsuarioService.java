package com.elian.Sitema_backend_turnos.service;

import com.elian.Sitema_backend_turnos.dto.CrearUsuarioDTO;
import com.elian.Sitema_backend_turnos.model.Usuario;
import com.elian.Sitema_backend_turnos.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepo;
    private final PasswordEncoder encoder;

    public UsuarioService(UsuarioRepository usuarioRepo,
                          PasswordEncoder encoder) {
        this.usuarioRepo = usuarioRepo;
        this.encoder = encoder;
    }

    public Usuario crearUsuario(CrearUsuarioDTO dto) {

        if (usuarioRepo.findByUsername(dto.username())) {
            throw new RuntimeException("Usuario ya existe");
        }

        Usuario u = new Usuario();
        u.setUsername(dto.username());
        u.setPassword(encoder.encode(dto.password()));
        u.setRol(dto.rol());

        return usuarioRepo.save(u);
    }
}

