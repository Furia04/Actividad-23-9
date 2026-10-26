package com.actividad.crud.controller;

import com.actividad.crud.dto.ReservaDTO;
import com.actividad.crud.dto.ReservaResponseDTO;
import com.actividad.crud.service.ReservaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/reservas", "/reservas/"})
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    @GetMapping
    public ResponseEntity<List<ReservaResponseDTO>> listar() {
        return ResponseEntity.ok(reservaService.listar());
    }

    @GetMapping({"/{id}", "/{id}/"})
    public ResponseEntity<ReservaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.buscarPorId(id));
    }

    @GetMapping({"/usuario/{usuarioId}", "/usuario/{usuarioId}/"})
    public ResponseEntity<List<ReservaResponseDTO>> buscarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(reservaService.buscarPorUsuarioId(usuarioId));
    }

    @PostMapping
    public ResponseEntity<ReservaResponseDTO> crear(@Valid @RequestBody ReservaDTO dto) {
        ReservaResponseDTO nuevaReserva = reservaService.crear(dto);
        return new ResponseEntity<>(nuevaReserva, HttpStatus.CREATED);
    }

    @PutMapping({"/{id}", "/{id}/"})
    public ResponseEntity<ReservaResponseDTO> modificar(@PathVariable Long id, @Valid @RequestBody ReservaDTO dto) {
        ReservaResponseDTO reservaActualizada = reservaService.modificar(id, dto);
        return ResponseEntity.ok(reservaActualizada);
    }

    @DeleteMapping({"/{id}", "/{id}/"})
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        reservaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
