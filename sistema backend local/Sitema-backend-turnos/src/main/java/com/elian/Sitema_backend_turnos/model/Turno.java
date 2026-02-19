package com.elian.Sitema_backend_turnos.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(
        name = "turnos",
        uniqueConstraints = @UniqueConstraint(columnNames = {"profesional_id", "fecha", "hora"})
)
public class Turno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "profesional_id", nullable = false)
    private Profesional profesional;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private LocalTime hora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoTurno estado;

    public Turno() {
    }

    public Turno(Cliente cliente, Profesional profesional, LocalDate fecha, LocalTime hora) {
        this.cliente = cliente;
        this.profesional = profesional;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = EstadoTurno.PENDIENTE;
    }

    // getters y setters

    public Long getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Profesional getProfesional() {
        return profesional;
    }


    public LocalDate getFecha() {
        return fecha;
    }


    public LocalTime getHora() {
        return hora;
    }

    public EstadoTurno getEstado() {
        return estado;
    }

}


