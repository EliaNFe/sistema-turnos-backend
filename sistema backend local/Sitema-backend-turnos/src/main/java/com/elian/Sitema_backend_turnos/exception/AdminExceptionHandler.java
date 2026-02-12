package com.elian.Sitema_backend_turnos.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class AdminExceptionHandler {        //ESTE ES PARA EXCEPTION DE VISTAS Y EL GLOBAL PARA LAS DE API CON JSON

    @ExceptionHandler({
            ClientenotFoundException.class,
            ProfesionalNotFoundException.class,
            TurnoNotFoundException.class,
            IllegalArgumentException.class,
            IllegalStateException.class
    })
    public String manejarErroresAdmin(Exception ex,
                                      RedirectAttributes redirect) {

        redirect.addFlashAttribute("error", ex.getMessage());

        return "redirect:/admin/dashboard";
    }
}
