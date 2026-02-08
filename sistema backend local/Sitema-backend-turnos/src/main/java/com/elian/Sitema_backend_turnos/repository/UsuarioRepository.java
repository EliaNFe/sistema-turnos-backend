package com.elian.Sitema_backend_turnos.repository;

import com.elian.Sitema_backend_turnos.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByUsername(String username);
    Optional<Usuario> findByUsername(String username);
}
