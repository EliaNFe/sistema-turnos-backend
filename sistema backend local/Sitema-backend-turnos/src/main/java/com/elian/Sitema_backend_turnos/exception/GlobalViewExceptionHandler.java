package com.elian.Sitema_backend_turnos.exception;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalViewExceptionHandler {
    @ControllerAdvice
    public static class globalViewExceptionHandler {

        @ExceptionHandler(NullPointerException.class)
        public String handleNullPointer(Model model) {
            model.addAttribute("error", "Hubo un problema con la configuración de tu perfil. Contacta al administrador.");
            return "error"; // página Thymeleaf
        }
    }
}


