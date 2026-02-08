package com.elian.Sitema_backend_turnos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller         //usamos controller y no restcontroller porque devuelve vistas, anotadoo
public class WebController {

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}

