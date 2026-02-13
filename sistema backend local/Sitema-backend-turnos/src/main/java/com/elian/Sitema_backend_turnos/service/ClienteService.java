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
        if (clienteRepository.existsByEmail(dto.email())) {
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


    public List<ClienteDTO> listarClientesActivosSinPaginacion() {
        return clienteRepository.findByActivoTrue()
                .stream()
                .map(ClienteMapper::toDTO) // Usamos tu mapper existente
                .toList();
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

    public void desactivarCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        cliente.setActivo(false);
        clienteRepository.save(cliente);
    }

    public void activarCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        cliente.setActivo(true);
        clienteRepository.save(cliente);
    }


    public List<ClienteDTO> listarClientesActivos() {
        return clienteRepository.findAll().stream()
                .filter(Cliente::isActivo)
                .map(ClienteMapper::toDTO)
                .toList();
    }
}