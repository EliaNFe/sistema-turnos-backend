package com.elian.Sitema_backend_turnos.exception;

import com.elian.Sitema_backend_turnos.controller.AdminController;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice(assignableTypes = {AdminController.class})
public class AdminExceptionHandler {

    @ExceptionHandler(org.springframework.transaction.TransactionSystemException.class)
    public String handleTransactionException(org.springframework.transaction.TransactionSystemException ex,
                                             RedirectAttributes redirectAttributes,
                                             HttpServletRequest request) {
        Throwable cause = ex.getRootCause();
        String mensaje = "Error en la transacción";

        if (cause instanceof jakarta.validation.ConstraintViolationException) {
            jakarta.validation.ConstraintViolationException consEx = (jakarta.validation.ConstraintViolationException) cause;
            mensaje = consEx.getConstraintViolations().iterator().next().getMessage();
        }

        redirectAttributes.addFlashAttribute("error", mensaje);
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/admin/dashboard");
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public String manejarErroresManuales(RuntimeException ex, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/admin/dashboard");
    }
}
