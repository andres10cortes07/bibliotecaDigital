package com.example.recursos.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "recursos")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties("ejemplares")
public class Recurso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    @JsonProperty("id")
    private Long id;

    @Column(nullable = false)
    @Size(max = 50, message = "El título no debe exceder los 50 caracteres")
    @JsonProperty("titulo")
    private String titulo;

    @Column(nullable = false)
    @Size(max = 50, message = "El autor no debe exceder los 50 caracteres")
    @JsonProperty("autor")
    private String autor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @JsonProperty("tipo_recurso")
    private Tipo tipo_recurso;

    @Column(nullable = true)
    @JsonProperty("fecha_publicacion")
    private LocalDate fecha_publicacion;

    public enum Tipo {
        libro, revista, otro
    }
}
