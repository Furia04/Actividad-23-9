package com.actividad.crud.mapper;

import com.actividad.crud.dto.UsuarioDTO;
import com.actividad.crud.dto.UsuarioRegistroDTO;
import com.actividad.crud.dto.UsuarioResponseDTO;
import com.actividad.crud.model.Rol;
import com.actividad.crud.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public Usuario toEntity(UsuarioRegistroDTO dto, String hashedPassword) {
        if (dto == null) {
            return null;
        }
        return Usuario.builder()
                .nombre(dto.getNombre().trim())
                .correo(dto.getCorreo().trim())
                .edad(dto.getEdad())
                .password(hashedPassword)
                .rol(Rol.USER)
                .build();
    }

    public UsuarioResponseDTO toResponseDTO(Usuario entity) {
        if (entity == null) {
            return null;
        }
        return UsuarioResponseDTO.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .correo(entity.getCorreo())
                .edad(entity.getEdad())
                .rol(entity.getRol())
                .build();
    }

    public void updateEntityFromDTO(Usuario entity, UsuarioDTO dto) {
        if (dto.getNombre() != null && !dto.getNombre().isBlank()) {
            entity.setNombre(dto.getNombre().trim());
        }
        if (dto.getCorreo() != null && !dto.getCorreo().isBlank()) {
            entity.setCorreo(dto.getCorreo().trim());
        }
        if (dto.getEdad() != null) {
            entity.setEdad(dto.getEdad());
        }
    }
}
