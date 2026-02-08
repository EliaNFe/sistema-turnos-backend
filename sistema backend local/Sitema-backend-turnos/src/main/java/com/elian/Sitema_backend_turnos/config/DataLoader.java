package com.elian.Sitema_backend_turnos.config;

import com.elian.Sitema_backend_turnos.model.Rol;
import com.elian.Sitema_backend_turnos.model.Usuario;
import com.elian.Sitema_backend_turnos.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder encoder;

    public DataLoader(UsuarioRepository usuarioRepository,
                      PasswordEncoder encoder) {

        this.usuarioRepository = usuarioRepository;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {

        if (usuarioRepository.findByUsername("admin").isEmpty()) {

            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword(encoder.encode("admin123"));
            admin.setRol(Rol.ADMIN);

            usuarioRepository.save(admin);
        }

        if (usuarioRepository.findByUsername("pro").isEmpty()) {

            Usuario pro = new Usuario();
            pro.setUsername("pro");
            pro.setPassword(encoder.encode("pro123"));
            pro.setRol(Rol.PROFESIONAL);

            usuarioRepository.save(pro);
        }
    }
}
