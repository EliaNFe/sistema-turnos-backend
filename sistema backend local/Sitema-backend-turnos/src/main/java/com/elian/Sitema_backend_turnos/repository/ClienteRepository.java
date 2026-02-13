package com.elian.Sitema_backend_turnos.repository;

import com.elian.Sitema_backend_turnos.model.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByDocumento(String documento);
    boolean existsByDocumentoAndIdNot(String documento, Long id);
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByEmail(String email);
    Page<Cliente> findByActivoTrue(Pageable pageable);
    List<Cliente> findByActivoTrue();
}