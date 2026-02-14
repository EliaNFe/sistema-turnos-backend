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

        // Crear el Administrador si no existe
        if (usuarioRepository.findByUsername("admin").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword(encoder.encode("admin123"));
            admin.setRol(Rol.ADMIN);
            // El admin no tiene profesional_id, por lo que pasa el Check Constraint
            usuarioRepository.save(admin);
            System.out.println("Carga Inicial: Usuario Admin creado con éxito.");
        }

        /* COMENTADO PARA EVITAR VIOLACIÓN DE CONSTRAINT:
           El usuario con rol PROFESIONAL exige un profesional_id en la DB.
           Es mejor crearlos desde el panel de administración una vez iniciada la app.
        */
        /*
        if (usuarioRepository.findByUsername("pro").isEmpty()) {
            Usuario pro = new Usuario();
            pro.setUsername("pro");
            pro.setPassword(encoder.encode("pro123"));
            pro.setRol(Rol.PROFESIONAL);
            usuarioRepository.save(pro);
        }
        */
    }
}