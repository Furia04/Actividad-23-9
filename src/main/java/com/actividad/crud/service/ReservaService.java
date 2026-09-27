package com.actividad.crud.service;

import com.actividad.crud.dto.ReservaDTO;
import com.actividad.crud.exception.ReservaNoEncontradaException;
import com.actividad.crud.exception.UsuarioNoEncontradoException;
import com.actividad.crud.exception.ValidacionException;
import com.actividad.crud.model.Reserva;
import com.actividad.crud.model.Usuario;
import com.actividad.crud.repository.ReservaRepository;
import com.actividad.crud.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<Reserva> listar() {
        log.info("Consultando todas las reservas");
        return reservaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Reserva buscarPorId(Long id) {
        log.info("Buscando reserva con id: {}", id);
        return reservaRepository.findById(id)
                .orElseThrow(() -> new ReservaNoEncontradaException(id));
    }

    @Transactional
    public Reserva crear(ReservaDTO dto) {
        log.info("Creando reserva para usuarioId: {}", dto.getUsuarioId());

        if (dto.getCantidadPersonas() == null || dto.getCantidadPersonas() < 1) {
            throw new ValidacionException("La cantidad de personas debe ser de al menos 1");
        }

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new UsuarioNoEncontradoException(dto.getUsuarioId()));

        Reserva reserva = Reserva.builder()
                .fechaHora(dto.getFechaHora())
                .cantidadPersonas(dto.getCantidadPersonas())
                .observaciones(dto.getObservaciones())
                .usuario(usuario)
                .build();

        return reservaRepository.save(reserva);
    }

    @Transactional
    public Reserva modificar(Long id, ReservaDTO dto) {
        log.info("Modificando reserva con id: {}", id);

        Reserva reservaExistente = buscarPorId(id);

        if (dto.getCantidadPersonas() == null || dto.getCantidadPersonas() < 1) {
            throw new ValidacionException("La cantidad de personas debe ser de al menos 1");
        }

        if (dto.getUsuarioId() != null && !dto.getUsuarioId().equals(reservaExistente.getUsuario().getId())) {
            Usuario nuevoUsuario = usuarioRepository.findById(dto.getUsuarioId())
                    .orElseThrow(() -> new UsuarioNoEncontradoException(dto.getUsuarioId()));
            reservaExistente.setUsuario(nuevoUsuario);
        }

        reservaExistente.setFechaHora(dto.getFechaHora());
        reservaExistente.setCantidadPersonas(dto.getCantidadPersonas());
        reservaExistente.setObservaciones(dto.getObservaciones());

        return reservaRepository.save(reservaExistente);
    }

    @Transactional
    public void eliminar(Long id) {
        log.info("Eliminando reserva con id: {}", id);
        Reserva reservaExistente = buscarPorId(id);
        reservaRepository.delete(reservaExistente);
    }
}
