package com.elian.Sitema_backend_turnos.exception;

public class ClientenotFoundException extends RuntimeException {

    public ClientenotFoundException(Long id) {
        super("Cliente no encontrado con id: " + id);
    }
}
