package com.actividad.crud.mapper;

import com.actividad.crud.dto.ReservaDTO;
import com.actividad.crud.dto.ReservaResponseDTO;
import com.actividad.crud.model.Reserva;
import com.actividad.crud.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservaMapper {

    private final UsuarioMapper usuarioMapper;

    public Reserva toEntity(ReservaDTO dto, Usuario usuario) {
        if (dto == null) {
            return null;
        }
        return Reserva.builder()
                .fechaHora(dto.getFechaHora())
                .cantidadPersonas(dto.getCantidadPersonas())
                .observaciones(dto.getObservaciones())
                .usuario(usuario)
                .build();
    }

    public ReservaResponseDTO toResponseDTO(Reserva entity) {
        if (entity == null) {
            return null;
        }
        return ReservaResponseDTO.builder()
                .id(entity.getId())
                .fechaHora(entity.getFechaHora())
                .cantidadPersonas(entity.getCantidadPersonas())
                .observaciones(entity.getObservaciones())
                .usuarioId(entity.getUsuario() != null ? entity.getUsuario().getId() : null)
                .usuario(usuarioMapper.toResponseDTO(entity.getUsuario()))
                .build();
    }
}
