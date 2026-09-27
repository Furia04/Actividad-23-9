package com.actividad.crud.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "El correo no puede estar vacío")
    @Email(message = "El formato de correo es inválido")
    @JsonAlias({"email", "mail"})
    @Column(nullable = false)
    private String correo;

    @Min(value = 0, message = "La edad no puede ser negativa")
    @Column(nullable = false)
    private Integer edad;
}
