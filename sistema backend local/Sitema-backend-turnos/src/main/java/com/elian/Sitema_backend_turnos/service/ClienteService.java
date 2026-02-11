package com.elian.Sitema_backend_turnos.service;

import com.elian.Sitema_backend_turnos.dto.CrearClienteDTO;
import com.elian.Sitema_backend_turnos.exception.ClientenotFoundException;
import com.elian.Sitema_backend_turnos.mapper.ClienteMapper;
import com.elian.Sitema_backend_turnos.model.Cliente;
import com.elian.Sitema_backend_turnos.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public Cliente crearCliente(CrearClienteDTO dto) {

        if (clienteRepository.existsByDocumento(dto.documento())) {
            throw new IllegalArgumentException("Ya existe un cliente con ese documento");
        }

        Cliente cliente = ClienteMapper.toEntity(dto);

        return clienteRepository.save(cliente);
    }


    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ClientenotFoundException(id));
    }

    public Cliente actualizar(Long id, Cliente cliente) {
        Cliente existente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClientenotFoundException(id));

        existente.setNombre(cliente.getNombre());
        existente.setApellido(cliente.getApellido());
        existente.setDocumento(cliente.getDocumento());
        existente.setTelefono(cliente.getTelefono());
        existente.setEmail(cliente.getEmail());

        return clienteRepository.save(existente);
    }

    public void eliminar(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new ClientenotFoundException(id);
        }
        clienteRepository.deleteById(id);
    }
}