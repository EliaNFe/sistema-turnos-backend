package com.elian.Sitema_backend_turnos.controller;

import com.elian.Sitema_backend_turnos.dto.ActualizarTurnoDTO;
import com.elian.Sitema_backend_turnos.dto.ClienteDTO;
import com.elian.Sitema_backend_turnos.dto.CrearTurnoDTO;
import com.elian.Sitema_backend_turnos.dto.TurnoDTO;
import com.elian.Sitema_backend_turnos.mapper.TurnoMapper;
import com.elian.Sitema_backend_turnos.model.Cliente;
import com.elian.Sitema_backend_turnos.service.ClienteService;
import com.elian.Sitema_backend_turnos.service.ProfesionalService;
import com.elian.Sitema_backend_turnos.service.TurnoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

        model.addAttribute(
                "turnos",
                turnoService.listarTodos()
        );

        return "admin-dashboard";
    }

    @PostMapping("/turnos/cancelar/{id}")
    public String cancelarTurno(@PathVariable Long id,
                                RedirectAttributes redirect) {

            turnoService.cancelarTurno(id);
            redirect.addFlashAttribute("success", "Turno cancelado");

        return "redirect:/admin/turnos";
    }


    @GetMapping("/clientes/nuevo")
    public String nuevoCliente(Model model) {

        model.addAttribute(
                "cliente",
                new ClienteDTO(null, null, null, null, null,null)
        );


        return "cliente-form";
    }




    @GetMapping("/turnos/nuevo")
    public String mostrarFormularioTurno(Model model) {
        model.addAttribute("turno", new CrearTurnoDTO(null, null, null, null));
        return "turno-form";
    }

    @PostMapping("/clientes/nuevo")
    public String crearCliente(@ModelAttribute ClienteDTO dto,
                               RedirectAttributes redirect) {

        clienteService.crearCliente(dto);
        redirect.addFlashAttribute("success", "Cliente agregado correctamente");
        return "redirect:/admin/clientes";
    }

    @GetMapping("/clientes")
    public String listarClientes(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "5") int size,
                                 Model model) {

        Page<ClienteDTO> clientesPage = clienteService.listarClientes(PageRequest.of(page, size));

        model.addAttribute("clientesPage", clientesPage);
        model.addAttribute("clientes", clientesPage);

        return "admin-clientes";
    }




    @PostMapping("/turnos/nuevo")
    public String crearTurno(@ModelAttribute CrearTurnoDTO dto) {

        turnoService.crearTurno(dto);

        return "redirect:/admin/dashboard";
    }

    @GetMapping("/turnos")
    public String listarTurnos(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String profesionalId,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha,
            Model model) {

        List<TurnoDTO> turnos;

        // limpiar valores vacíos
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

        model.addAttribute("profesionales",
                profesionalService.listarProfesionales());


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

    @GetMapping("/clientes/editar/{id}")
    public String editarCliente(@PathVariable Long id, Model model) {
        Cliente cliente = clienteService.buscarPorId(id);
        model.addAttribute("cliente", cliente);
        return "cliente-form-editar";
    }

    @PostMapping("/clientes/editar/{id}")
    public String actualizarCliente(@PathVariable Long id,
                                    @ModelAttribute Cliente cliente,
                                    RedirectAttributes redirect) {
        clienteService.actualizar(id, cliente);
        redirect.addFlashAttribute("success", "Cliente actualizado correctamente");
        return "redirect:/admin/clientes";
    }


}

