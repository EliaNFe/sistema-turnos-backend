package com.elian.Sitema_backend_turnos.controller;

import com.elian.Sitema_backend_turnos.dto.CrearProfesionalDTO;
import com.elian.Sitema_backend_turnos.dto.ProfesionalDTO;
import com.elian.Sitema_backend_turnos.service.ProfesionalService;
import com.elian.Sitema_backend_turnos.service.UsuarioService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


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
    public String listarProfesionales(@RequestParam(required = false) String nombre,
                                      @RequestParam(defaultValue = "0") int page,
                                      Model model) {

        Page<ProfesionalDTO> profesionalesPage = profesionalService.listarPaginado(nombre, page);

        model.addAttribute("profesionales", profesionalesPage.getContent());
        model.addAttribute("profesionalesPage", profesionalesPage); // Clave para el HTML
        return "admin-profesionales";
    }


    @GetMapping("/nuevo")
    public String formularioProfesional(Model model) {
        model.addAttribute("profesional", new CrearProfesionalDTO(null, null));
        return "profesional-form";
    }


    @PostMapping("/nuevo")
    public String guardarProfesional(@ModelAttribute CrearProfesionalDTO dto, RedirectAttributes redirect) {
        try {
            profesionalService.crearProfesional(dto);
            redirect.addFlashAttribute("success", "Profesional registrado con éxito");
        } catch (RuntimeException e) {
            // Si el service lanza la excepción de "Ya existe", la mandamos como error
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/profesionales/nuevo";
        }
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