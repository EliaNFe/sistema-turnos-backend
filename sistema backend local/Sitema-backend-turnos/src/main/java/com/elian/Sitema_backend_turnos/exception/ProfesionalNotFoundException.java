package com.elian.Sitema_backend_turnos.exception;

public class ProfesionalNotFoundException extends RuntimeException {

    public ProfesionalNotFoundException(Long id) {
        super("Profesional con id " + id + " no encontrado");
    }
}
