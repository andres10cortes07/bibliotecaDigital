package com.example.usuarios.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
    @Column(nullable = false, updatable = false)
    @JsonProperty("id")
    private Long id;

    @Column(nullable = false)
    @Size(max = 50, message = "El nombre no debe exceder los 50 caracteres")
    @JsonProperty("nombre")
    private String nombre;

    @Column(unique = true, nullable = false)
    @Email(message = "El correo debe tener un formato válido")
    @JsonProperty("email")
    private String email;

    @Column(unique = true, nullable = false)
    @Size(min = 6, max = 11, message = "El teléfono debe tener entre 6 y 11 caracteres")
    @JsonProperty("telefono")
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @JsonProperty("rol")
    private Rol rol;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    @JsonProperty("fecha_registro")
    private Date fechaRegistro;

    @Column(nullable = false)
    @JsonProperty(value = "contrasena", access = JsonProperty.Access.WRITE_ONLY)
    private String contrasena;

    @PrePersist
    protected void onCreate() {
        this.fechaRegistro = new Date();
    }

    public enum Rol {
        estudiante, profesor, administrativo
    }

}
