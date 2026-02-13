package com.elian.Sitema_backend_turnos.repository;

import com.elian.Sitema_backend_turnos.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByDocumento(String documento);
    boolean existsByDocumentoAndIdNot(String documento, Long id);
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByEmail(String email);

}