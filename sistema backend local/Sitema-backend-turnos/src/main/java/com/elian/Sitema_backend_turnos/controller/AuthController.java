package com.elian.Sitema_backend_turnos.controller;
import com.elian.Sitema_backend_turnos.model.Usuario;
import com.elian.Sitema_backend_turnos.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/me")
    public Usuario me(Authentication auth) {

        CustomUserDetails user =
                (CustomUserDetails) auth.getPrincipal();

        return user.getUsuario();
    }
}


