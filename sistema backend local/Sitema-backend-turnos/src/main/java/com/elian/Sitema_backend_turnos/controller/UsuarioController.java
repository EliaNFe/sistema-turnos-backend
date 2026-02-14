package com.elian.Sitema_backend_turnos.controller;

import com.elian.Sitema_backend_turnos.dto.CrearUsuarioDTO;
import com.elian.Sitema_backend_turnos.model.Rol;
import com.elian.Sitema_backend_turnos.service.ProfesionalService;
import com.elian.Sitema_backend_turnos.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final ProfesionalService profesionalService;

    public UsuarioController(UsuarioService usuarioService, ProfesionalService profesionalService) {
        this.usuarioService = usuarioService;
        this.profesionalService = profesionalService;
    }

    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute CrearUsuarioDTO dto, RedirectAttributes redirect) {
        try {
            usuarioService.crearUsuario(dto);
            redirect.addFlashAttribute("success", "¡Usuario " + dto.username() + " creado con éxito!");
            return "redirect:/admin/profesionales";
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/usuarios/nuevo";
        }
    }

    @GetMapping("/nuevo")
    public String nuevoUsuario(@RequestParam(required = false) Long profesionalId, Model model) {
        model.addAttribute("profesionales", profesionalService.listarDisponiblesParaUsuario());
        model.addAttribute("roles", Rol.values());
        model.addAttribute("idPreseleccionado", profesionalId);
        return "admin-usuarios-form";
    }


}


