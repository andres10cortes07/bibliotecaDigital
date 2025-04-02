package com.example.usuarios.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor

public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    @JsonProperty("id")
    private Long id;

    @Column(nullable = false)
    @JsonProperty("nombre")
    private String nombre;

    @Column(unique = true, nullable = false)
    @JsonProperty("email")
    private String email;

    @Column(unique = true, nullable = false)
    @JsonProperty("telefono")
    private String telefono;

    @Enumerated(EnumType.STRING) // Guardar el enum como String en la base de datos
    @Column(nullable = false)
    @JsonProperty("rol")
    private Rol rol;

    @Temporal(TemporalType.TIMESTAMP) // Define que es un timestamp
    @Column(nullable = false, updatable = false)
    @JsonProperty("fecha_registro")
    private Date fechaRegistro;


    // CONFIGURACIONES PARA CAMPOS ESPECIALES
    @PrePersist //hace que se efectue antes de la creacion de un registro en la tabla
    protected void onCreate() {
        this.fechaRegistro = new Date(); // Asigna la fecha actual al crear el usuario
    }

    public enum Rol {
        estudiante, profesor, administrativo
    }

}