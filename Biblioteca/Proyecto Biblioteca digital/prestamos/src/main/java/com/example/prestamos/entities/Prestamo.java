package com.example.prestamos.entities;

import com.example.ejemplares.entities.Ejemplar;
import com.example.usuarios.entities.Usuario;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "prestamos")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    @JsonProperty("id")
    private Long id;

    @Column(nullable = false)
    @JsonProperty("fecha_prestamo")
    private LocalDate fecha_prestamo;

    @Column(nullable = false)
    @JsonProperty("fecha_devolucion")
    private LocalDate fecha_devolucion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ejemplares", nullable = false)
    private Ejemplar ejemplar;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuarios", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @JsonProperty("estado")
    private Prestamo.Estado estado;

    public enum Estado {
        activo, devuelto, atrasado
    }

}