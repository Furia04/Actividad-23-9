package com.actividad.crud.dto;

import com.actividad.crud.model.Rol;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponseDTO {

    private Long id;
    private String nombre;
    private String correo;
    private Integer edad;
    private Rol rol;
}
