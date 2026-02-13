package com.elian.Sitema_backend_turnos.controller;

import com.elian.Sitema_backend_turnos.dto.*;
import com.elian.Sitema_backend_turnos.mapper.ClienteMapper;
import com.elian.Sitema_backend_turnos.mapper.TurnoMapper;
import com.elian.Sitema_backend_turnos.model.Cliente;
import com.elian.Sitema_backend_turnos.service.ClienteService;
import com.elian.Sitema_backend_turnos.service.ProfesionalService;
import com.elian.Sitema_backend_turnos.service.TurnoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final TurnoService turnoService;
    private final ProfesionalService profesionalService;
    private final ClienteService clienteService;

    public AdminController(TurnoService turnoService, ProfesionalService profesionalService, ClienteService clienteService) {
        this.turnoService = turnoService;
        this.profesionalService = profesionalService;
        this.clienteService = clienteService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<TurnoDTO> todos = turnoService.listarTodos();
        LocalDate hoy = LocalDate.now();

        // 1. Contar turnos de hoy
        long turnosHoy = todos.stream().filter(t -> t.fecha().equals(hoy)).count();

        // 2. Contar pendientes globales
        long pendientes = todos.stream().filter(t -> t.estado().toString().equals("PENDIENTE")).count();

        model.addAttribute("cantidadHoy", turnosHoy);
        model.addAttribute("cantidadPendientes", pendientes);
        model.addAttribute("turnos", todos); // La tabla de abajo usa esta lista

        return "admin-dashboard";
    }

    //CLIENTESSSS

    @GetMapping("/clientes/nuevo")
    public String nuevoCliente(Model model) {
        model.addAttribute("cliente", new CrearClienteDTO(null, null, null, null, null));
        return "cliente-form";
    }


    @PostMapping("/clientes/desactivar/{id}")
    public String desactivarCliente(@PathVariable Long id, RedirectAttributes redirect) {
        clienteService.desactivarCliente(id);
        redirect.addFlashAttribute("success", "Cliente desactivado correctamente");
        return "redirect:/admin/clientes";
    }

    @PostMapping("/clientes/activar/{id}")
    public String activarCliente(@PathVariable Long id, RedirectAttributes redirect) {
        clienteService.activarCliente(id); // Este pone activo = true
        redirect.addFlashAttribute("success", "Cliente reactivado con éxito.");
        return "redirect:/admin/clientes";
    }

    @PostMapping("/clientes/nuevo")
    public String crearCliente(@ModelAttribute CrearClienteDTO dto,
                               RedirectAttributes redirect) {
        clienteService.crearCliente(dto); // ahora recibe CrearClienteDTO
        redirect.addFlashAttribute("success", "Cliente agregado correctamente");
        return "redirect:/admin/clientes";
    }


    @GetMapping("/clientes")
    public String listarClientes(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 Model model) {
        Page<ClienteDTO> clientesPage = clienteService.listarClientes(PageRequest.of(page, size));
        model.addAttribute("clientesPage", clientesPage);
        model.addAttribute("clientes", clientesPage.getContent()); // 👈 importante
        return "admin-clientes";
    }

    @GetMapping("/clientes/editar/{id}")
    public String editarCliente(@PathVariable Long id, Model model) {
        Cliente cliente = clienteService.buscarPorId(id);
        ClienteDTO dto = ClienteMapper.toDTO(cliente);
        model.addAttribute("cliente", dto);
        return "cliente-form-editar";
    }



    @PostMapping("/clientes/editar/{id}")
    public String actualizarCliente(@PathVariable Long id,
                                    @ModelAttribute ClienteDTO dto,
                                    RedirectAttributes redirect) {
        Cliente cliente = ClienteMapper.toEntityA(dto);
        clienteService.actualizar(id, cliente);
        redirect.addFlashAttribute("success", "Cliente actualizado correctamente");
        return "redirect:/admin/clientes";
    }

    // --- TURNOS ---

    @PostMapping("/turnos/cancelar/{id}")
    public String cancelarTurno(@PathVariable Long id,
                                RedirectAttributes redirect) {
        turnoService.cancelarTurno(id);
        redirect.addFlashAttribute("success", "Turno cancelado");
        return "redirect:/admin/turnos";
    }

    @GetMapping("/turnos/nuevo")
    public String nuevoTurno(Model model) {
        model.addAttribute("turno", new CrearTurnoDTO(null, null, null, null));

        // AQUÍ: Solo pasamos los que están activos para el combo/datalist
        model.addAttribute("clientes", clienteService.listarClientesActivosSinPaginacion());
        model.addAttribute("profesionales", profesionalService.listarActivos());

        return "turno-form";
    }

    @PostMapping("/turnos/nuevo")
    public String crearTurno(@ModelAttribute CrearTurnoDTO dto) {
        turnoService.crearTurno(dto);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/turnos")
    public String listarTurnos(@RequestParam(required = false) String estado,
                               @RequestParam(required = false) String profesionalId,
                               @RequestParam(required = false)
                               @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha,
                               Model model) {
        List<TurnoDTO> turnos;

        if (estado != null && estado.isBlank()) estado = null;
        if (profesionalId != null && profesionalId.isBlank()) profesionalId = null;

        if (estado != null) {
            turnos = turnoService.buscarPorEstado(estado);
        } else if (profesionalId != null) {
            turnos = turnoService.buscarPorProfesional(Long.parseLong(profesionalId));
        } else if (fecha != null) {
            turnos = turnoService.buscarPorFecha(fecha);
        } else {
            turnos = turnoService.listarTodos();
        }

        model.addAttribute("turnos", turnos);
        model.addAttribute("profesionales", profesionalService.listarProfesionales());
        return "admin-turnos";
    }

    @GetMapping("/turnos/editar/{id}")
    public String editarTurno(@PathVariable Long id, Model model) {
        TurnoDTO turno = turnoService.buscarPorId(id);
        ActualizarTurnoDTO dto = TurnoMapper.toActualizarDTO(turno);
        model.addAttribute("turno", dto);
        return "admin-turno-editar";
    }

    @PostMapping("/turnos/editar")
    public String guardarEdicion(@ModelAttribute ActualizarTurnoDTO dto,
                                 RedirectAttributes redirect) {
        turnoService.actualizarTurno(dto.id(), dto);
        redirect.addFlashAttribute("success", "Turno actualizado correctamente");
        return "redirect:/admin/turnos";
    }

    //-- profesionaless --

    @GetMapping("/profesionales")
    public String listarProfesionales(Model model) {
        // Mapeamos la lista de entidades a DTOs para la vista
        List<ProfesionalDTO> lista = profesionalService.listarProfesionales()
                .stream()
                .map(p -> new ProfesionalDTO(p.getId(), p.getNombre(), p.getEspecialidad(), p.isActivo()))
                .toList();

        model.addAttribute("profesionales", lista);
        return "admin-profesionales";
    }

    @GetMapping("/profesionales/nuevo")
    public String formularioProfesional(Model model) {
        // Usamos el record CrearProfesionalDTO con nulls para inicializar
        model.addAttribute("profesional", new CrearProfesionalDTO(null, null));
        return "profesional-form";
    }

    @PostMapping("/profesionales/nuevo")
    public String guardarProfesional(@ModelAttribute CrearProfesionalDTO dto, RedirectAttributes redirect) {
        profesionalService.crearProfesional(dto);
        redirect.addFlashAttribute("success", "Profesional registrado con éxito");
        return "redirect:/admin/profesionales";
    }

    @PostMapping("/profesionales/desactivar/{id}")
    public String desactivarProfesional(@PathVariable Long id, RedirectAttributes redirect) {
        profesionalService.desactivar(id);
        redirect.addFlashAttribute("success", "El profesional ha sido desactivado.");
        return "redirect:/admin/profesionales";
    }

    @PostMapping("/profesionales/activar/{id}")
    public String activarProfesional(@PathVariable Long id, RedirectAttributes redirect) {
        profesionalService.activar(id); // Debes crear el método .activar(id) en tu ProfesionalService
        redirect.addFlashAttribute("success", "El profesional ha sido reactivado.");
        return "redirect:/admin/profesionales";
    }

}