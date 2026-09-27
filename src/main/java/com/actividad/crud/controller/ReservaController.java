package com.actividad.crud.controller;

import com.actividad.crud.dto.ReservaDTO;
import com.actividad.crud.model.Reserva;
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
    public ResponseEntity<List<Reserva>> listar() {
        return ResponseEntity.ok(reservaService.listar());
    }

    @GetMapping({"/{id}", "/{id}/"})
    public ResponseEntity<Reserva> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Reserva> crear(@Valid @RequestBody ReservaDTO dto) {
        Reserva nuevaReserva = reservaService.crear(dto);
        return new ResponseEntity<>(nuevaReserva, HttpStatus.CREATED);
    }

    @PutMapping({"/{id}", "/{id}/"})
    public ResponseEntity<Reserva> modificar(@PathVariable Long id, @Valid @RequestBody ReservaDTO dto) {
        Reserva reservaActualizada = reservaService.modificar(id, dto);
        return ResponseEntity.ok(reservaActualizada);
    }

    @DeleteMapping({"/{id}", "/{id}/"})
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        reservaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
