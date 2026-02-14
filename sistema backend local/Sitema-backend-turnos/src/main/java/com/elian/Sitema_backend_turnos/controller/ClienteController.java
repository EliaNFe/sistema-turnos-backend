package com.elian.Sitema_backend_turnos.controller;
import com.elian.Sitema_backend_turnos.dto.ClienteDTO;
import com.elian.Sitema_backend_turnos.dto.CrearClienteDTO;
import com.elian.Sitema_backend_turnos.mapper.ClienteMapper;
import com.elian.Sitema_backend_turnos.model.Cliente;
import com.elian.Sitema_backend_turnos.service.ClienteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/clientes") // Mantenemos la ruta base para el Admin
@PreAuthorize("hasRole('ADMIN')")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // Listado de clientes con paginación
    @GetMapping
    public String listarClientes(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 Model model) {
        Page<ClienteDTO> clientesPage = clienteService.listarClientes(PageRequest.of(page, size));
        model.addAttribute("clientesPage", clientesPage);
        model.addAttribute("clientes", clientesPage.getContent());
        return "admin-clientes";
    }

    // Formulario para nuevo cliente
    @GetMapping("/nuevo")
    public String nuevoCliente(Model model) {
        model.addAttribute("cliente", new CrearClienteDTO(null, null, null, null, null));
        return "cliente-form";
    }

    // Guardar nuevo cliente
    @PostMapping("/nuevo")
    public String crearCliente(@ModelAttribute CrearClienteDTO dto,
                               RedirectAttributes redirect) {
        clienteService.crearCliente(dto);
        redirect.addFlashAttribute("success", "Cliente agregado correctamente");
        return "redirect:/admin/clientes";
    }

    // Formulario de edición
    @GetMapping("/editar/{id}")
    public String editarCliente(@PathVariable Long id, Model model) {
        Cliente cliente = clienteService.buscarPorId(id);
        ClienteDTO dto = ClienteMapper.toDTO(cliente);
        model.addAttribute("cliente", dto);
        return "cliente-form-editar";
    }

    // Procesar edición
    @PostMapping("/editar/{id}")
    public String actualizarCliente(@PathVariable Long id,
                                    @ModelAttribute ClienteDTO dto,
                                    RedirectAttributes redirect) {
        Cliente cliente = ClienteMapper.toEntityA(dto);
        clienteService.actualizar(id, cliente);
        redirect.addFlashAttribute("success", "Cliente actualizado correctamente");
        return "redirect:/admin/clientes";
    }

    // Desactivar (Baja lógica)
    @PostMapping("/desactivar/{id}")
    public String desactivarCliente(@PathVariable Long id, RedirectAttributes redirect) {
        clienteService.desactivarCliente(id);
        redirect.addFlashAttribute("success", "Cliente desactivado correctamente");
        return "redirect:/admin/clientes";
    }

    // Activar (Reactivación)
    @PostMapping("/activar/{id}")
    public String activarCliente(@PathVariable Long id, RedirectAttributes redirect) {
        clienteService.activarCliente(id);
        redirect.addFlashAttribute("success", "Cliente reactivado con éxito.");
        return "redirect:/admin/clientes";
    }
}