package com.elian.Sitema_backend_turnos.mapper;

import com.elian.Sitema_backend_turnos.dto.ClienteDTO;
import com.elian.Sitema_backend_turnos.model.Cliente;

public class ClienteMapper {

    public static Cliente toEntity(ClienteDTO dto) {
        Cliente c = new Cliente();

        c.setNombre(dto.nombre());
        c.setApellido(dto.apellido());
        c.setDocumento(dto.documento());
        c.setTelefono(dto.telefono());
        c.setEmail(dto.email());

        return c;
    }

    public static ClienteDTO toDTO(Cliente c) {
        if (c == null) {
            return null;
        }
        return new ClienteDTO(
                c.getNombre(),
                c.getApellido(),
                c.getDocumento(),
                c.getTelefono(),
                c.getEmail(),
                c.getId()
        );
    }



}
