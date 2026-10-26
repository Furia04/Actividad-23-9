package com.actividad.crud.service;

import com.actividad.crud.dto.ReservaDTO;
import com.actividad.crud.dto.ReservaResponseDTO;
import com.actividad.crud.exception.ReservaNoEncontradaException;
import com.actividad.crud.exception.ValidacionException;
import com.actividad.crud.mapper.ReservaMapper;
import com.actividad.crud.model.Reserva;
import com.actividad.crud.model.Usuario;
import com.actividad.crud.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final UsuarioService usuarioService;
    private final ReservaMapper reservaMapper;

    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> listar() {
        log.info("Consultando todas las reservas");
        return reservaRepository.findAll().stream()
                .map(reservaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReservaResponseDTO buscarPorId(Long id) {
        log.info("Buscando reserva con id: {}", id);
        Reserva reserva = buscarEntidadPorId(id);
        return reservaMapper.toResponseDTO(reserva);
    }

    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> buscarPorUsuarioId(Long usuarioId) {
        log.info("Consultando reservas asociadas al usuarioId: {}", usuarioId);
        // Validamos que el usuario exista
        usuarioService.buscarEntidadPorId(usuarioId);
        return reservaRepository.findByUsuario_Id(usuarioId).stream()
                .map(reservaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Reserva buscarEntidadPorId(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new ReservaNoEncontradaException(id));
    }

    @Transactional
    public ReservaResponseDTO crear(ReservaDTO dto) {
        log.info("Creando reserva para usuarioId: {}", dto.getUsuarioId());

        if (dto.getCantidadPersonas() == null || dto.getCantidadPersonas() < 1) {
            throw new ValidacionException("La cantidad de personas debe ser de al menos 1");
        }

        Usuario usuario = usuarioService.buscarEntidadPorId(dto.getUsuarioId());
        Reserva reserva = reservaMapper.toEntity(dto, usuario);
        Reserva guardada = reservaRepository.save(reserva);

        return reservaMapper.toResponseDTO(guardada);
    }

    @Transactional
    public ReservaResponseDTO modificar(Long id, ReservaDTO dto) {
        log.info("Modificando reserva con id: {}", id);

        Reserva reservaExistente = buscarEntidadPorId(id);

        if (dto.getCantidadPersonas() == null || dto.getCantidadPersonas() < 1) {
            throw new ValidacionException("La cantidad de personas debe ser de al menos 1");
        }

        if (dto.getUsuarioId() != null && !dto.getUsuarioId().equals(reservaExistente.getUsuario().getId())) {
            Usuario nuevoUsuario = usuarioService.buscarEntidadPorId(dto.getUsuarioId());
            reservaExistente.setUsuario(nuevoUsuario);
        }

        reservaExistente.setFechaHora(dto.getFechaHora());
        reservaExistente.setCantidadPersonas(dto.getCantidadPersonas());
        reservaExistente.setObservaciones(dto.getObservaciones());

        Reserva actualizada = reservaRepository.save(reservaExistente);
        return reservaMapper.toResponseDTO(actualizada);
    }

    @Transactional
    public void eliminar(Long id) {
        log.info("Eliminando reserva con id: {}", id);
        Reserva reservaExistente = buscarEntidadPorId(id);
        reservaRepository.delete(reservaExistente);
    }
}
