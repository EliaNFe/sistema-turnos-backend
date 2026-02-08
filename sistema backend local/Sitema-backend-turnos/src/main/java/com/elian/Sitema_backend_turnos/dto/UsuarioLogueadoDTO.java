package com.elian.Sitema_backend_turnos.dto;

import lombok.Getter;

@Getter
public class UsuarioLogueadoDTO {

    private String username;
    private String rol;
    private Long profesionalId;



    public UsuarioLogueadoDTO(String username, String rol, Long profesionalId) {
        this.username = username;
        this.rol = rol;
        this.profesionalId = profesionalId;
    }

    // getters
}
