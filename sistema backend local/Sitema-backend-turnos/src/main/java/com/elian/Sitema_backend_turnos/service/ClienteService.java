package com.elian.Sitema_backend_turnos.service;

import com.elian.Sitema_backend_turnos.dto.ClienteDTO;
import com.elian.Sitema_backend_turnos.dto.CrearClienteDTO;
import com.elian.Sitema_backend_turnos.exception.ClientenotFoundException;
import com.elian.Sitema_backend_turnos.mapper.ClienteMapper;
import com.elian.Sitema_backend_turnos.model.Cliente;
import com.elian.Sitema_backend_turnos.repository.ClienteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        if (clienteRepository.existsByDocumento(dto.email())) {
            throw new IllegalArgumentException("Ya existe un cliente con ese email");
        }

        if (clienteRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("Ya existe otro cliente con el email: " + dto.email());
        }
        Cliente cliente = new Cliente();
        cliente.setNombre(dto.nombre());
        cliente.setApellido(dto.apellido());
        cliente.setDocumento(dto.documento());
        cliente.setTelefono(dto.telefono());
        cliente.setEmail(dto.email());
        return clienteRepository.save(cliente);
    }


    public Page<ClienteDTO> listarClientes(Pageable pageable) {
        return clienteRepository.findAll(pageable)
                .map(ClienteMapper::toDTO);
    }

    public List<ClienteDTO> listarClientesSinPaginacion() {
        return clienteRepository.findAll()
                .stream()
                .map(ClienteMapper::toDTO)
                .toList();
    }


    @Transactional
    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ClientenotFoundException(id));
    }

    public Cliente actualizar(Long id, Cliente cliente) {
        //  Buscamos el cliente actual
        Cliente existente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClientenotFoundException(id));

        // Validar Documento único (exceptuando al cliente actual)
        if (clienteRepository.existsByDocumentoAndIdNot(cliente.getDocumento(), id)) {
            throw new IllegalArgumentException("Ya existe otro cliente con el documento: " + cliente.getDocumento());
        }

        //Validar Email único (exceptuando al cliente actual)
        if (clienteRepository.existsByEmailAndIdNot(cliente.getEmail(), id)) {
            throw new IllegalArgumentException("Ya existe otro cliente con el email: " + cliente.getEmail());
        }

        // 4. Mapeo de datos
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