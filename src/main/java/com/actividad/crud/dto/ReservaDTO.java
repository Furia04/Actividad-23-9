package com.actividad.crud.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaDTO {

    private Long id;

    @NotNull(message = "La fecha y hora son obligatorias")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaHora;

    @NotNull(message = "La cantidad de personas es obligatoria")
    @Min(value = 1, message = "La reserva debe ser para al menos 1 persona")
    private Integer cantidadPersonas;

    private String observaciones;

    @NotNull(message = "El usuarioId es obligatorio")
    private Long usuarioId;
}
