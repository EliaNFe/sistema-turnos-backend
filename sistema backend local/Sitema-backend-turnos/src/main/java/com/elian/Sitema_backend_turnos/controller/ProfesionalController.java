package com.elian.Sitema_backend_turnos.controller;

import com.elian.Sitema_backend_turnos.dto.CrearProfesionalDTO;
import com.elian.Sitema_backend_turnos.dto.ProfesionalDTO;
import com.elian.Sitema_backend_turnos.service.ProfesionalService;
import com.elian.Sitema_backend_turnos.service.UsuarioService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/profesionales")
@PreAuthorize("hasRole('ADMIN')")
public class ProfesionalController {

    private final ProfesionalService profesionalService;
    private final UsuarioService usuarioService;
    public ProfesionalController(ProfesionalService profesionalService, UsuarioService usuarioService) {
        this.profesionalService = profesionalService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listarProfesionales(Model model) {
        List<ProfesionalDTO> lista = profesionalService.listarProfesionales()
                .stream()
                .map(p -> new ProfesionalDTO(
                        p.getId(),
                        p.getNombre(),
                        p.getEspecialidad(),
                        p.isActivo(),
                        usuarioService.tieneUsuarioAsociado(p.getId())
                ))
                .toList();

        model.addAttribute("profesionales", lista);
        return "admin-profesionales";
    }


    @GetMapping("/nuevo")
    public String formularioProfesional(Model model) {
        model.addAttribute("profesional", new CrearProfesionalDTO(null, null));
        return "profesional-form";
    }


    @PostMapping("/nuevo")
    public String guardarProfesional(@ModelAttribute CrearProfesionalDTO dto, RedirectAttributes redirect) {
        profesionalService.crearProfesional(dto);
        redirect.addFlashAttribute("success", "Profesional registrado con éxito. Ya puedes asignarle un usuario.");
        return "redirect:/admin/profesionales";
    }


    @PostMapping("/desactivar/{id}")
    public String desactivarProfesional(@PathVariable Long id, RedirectAttributes redirect) {
        profesionalService.desactivar(id);
        redirect.addFlashAttribute("success", "El profesional ha sido desactivado.");
        return "redirect:/admin/profesionales";
    }

    @PostMapping("/activar/{id}")
    public String activarProfesional(@PathVariable Long id, RedirectAttributes redirect) {
        profesionalService.activar(id);
        redirect.addFlashAttribute("success", "El profesional ha sido reactivado correctamente.");
        return "redirect:/admin/profesionales";
    }
}