package com.elian.Sitema_backend_turnos.controller;

import com.elian.Sitema_backend_turnos.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller         //usamos controller y no restcontroller porque devuelve vistas, anotadoo
public class WebController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/redirect")
    public String redirect(Authentication auth) {

        CustomUserDetails user =
                (CustomUserDetails) auth.getPrincipal();

        switch (user.getUsuario().getRol()) {

            case ADMIN:
                return "redirect:/admin-dashboard";

            case PROFESIONAL:
                return "redirect:/pro-dashboard";

            default:
                return "redirect:/login";
        }
    }
}

