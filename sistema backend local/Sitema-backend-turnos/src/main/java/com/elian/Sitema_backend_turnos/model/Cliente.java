package com.elian.Sitema_backend_turnos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {

    public Cliente(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean activo = true;

    @NotBlank
    @Column(nullable = false)
    private String nombre;

    @NotBlank
    @Column(nullable = false)
    private String apellido;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String documento;

    @NotBlank
    @Column(nullable = false)
    private String telefono;

    // ... dentro de la clase Cliente
    @Email(message = "El formato del correo no es válido")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)\\.(com|ar|net|org)$",
            message = "El email debe terminar en una extensión válida (.com, .ar, etc.)")
    @NotBlank
    private String email;

}
