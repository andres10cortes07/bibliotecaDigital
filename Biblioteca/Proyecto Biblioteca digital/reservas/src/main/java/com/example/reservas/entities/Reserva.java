package com.example.reservas.entities;

import com.example.ejemplares.entities.Ejemplar;
import com.example.usuarios.entities.Usuario;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "reservas")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    @JsonProperty("id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ejemplares", nullable = false)
    private Ejemplar ejemplar;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuarios", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    @JsonProperty("fecha_reserva")
    private LocalDate fecha_reserva;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @JsonProperty("estado")
    private Reserva.Estado estado;

    public enum Estado {
        activa, cancelada, completada
    }
}
