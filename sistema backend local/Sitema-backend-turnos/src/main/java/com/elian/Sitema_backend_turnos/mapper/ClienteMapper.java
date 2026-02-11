package com.elian.Sitema_backend_turnos.mapper;

import com.elian.Sitema_backend_turnos.dto.CrearClienteDTO;
import com.elian.Sitema_backend_turnos.model.Cliente;

public class ClienteMapper {

    public static Cliente toEntity(CrearClienteDTO dto) {
        Cliente c = new Cliente();

        c.setNombre(dto.nombre());
        c.setDocumento(dto.documento());
        c.setEmail(dto.email());

        return c;
    }
}
