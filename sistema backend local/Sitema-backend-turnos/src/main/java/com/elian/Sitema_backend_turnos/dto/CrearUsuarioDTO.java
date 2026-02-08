package com.elian.Sitema_backend_turnos.dto;

import com.elian.Sitema_backend_turnos.model.Rol;

public record CrearUsuarioDTO(
        String username,
        String password,
        Rol rol,
        Long profesionalId
) {}
