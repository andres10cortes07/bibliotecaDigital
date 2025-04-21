package com.example.ejemplares.entities;

import com.example.recursos.entities.Recurso;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "ejemplares")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Ejemplar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    @JsonProperty("id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recurso_id", nullable = false)
    private Recurso recurso;

    @Column(nullable = false, unique = true)
    @Size(max = 50, message = "El código del ejemplar no debe exceder los 50 caracteres")
    @JsonProperty("codigo_ejemplar")
    private String codigo_ejemplar;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @JsonProperty("estado")
    private Estado estado = Estado.disponible; // valor por defecto

    public enum Estado {
        disponible, prestado, reservado
    }
}
