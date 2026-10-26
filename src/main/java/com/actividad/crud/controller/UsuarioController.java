package com.actividad.crud.controller;

import com.actividad.crud.dto.UsuarioDTO;
import com.actividad.crud.dto.UsuarioRegistroDTO;
import com.actividad.crud.dto.UsuarioResponseDTO;
import com.actividad.crud.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/usuarios", "/usuarios/"})
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listar() {
        return ResponseEntity.ok(usuarioService.listar());
    }

    @GetMapping({"/{id}", "/{id}/"})
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @PostMapping({"/registro", "/registro/"})
    public ResponseEntity<UsuarioResponseDTO> registrarPublico(@Valid @RequestBody UsuarioRegistroDTO dto) {
        UsuarioResponseDTO nuevoUsuario = usuarioService.registrar(dto);
        return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crear(@Valid @RequestBody UsuarioRegistroDTO dto) {
        UsuarioResponseDTO nuevoUsuario = usuarioService.registrar(dto);
        return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
    }

    @PutMapping({"/{id}", "/{id}/"})
    public ResponseEntity<UsuarioResponseDTO> modificar(@PathVariable Long id, @Valid @RequestBody UsuarioDTO dto) {
        UsuarioResponseDTO usuarioActualizado = usuarioService.modificar(id, dto);
        return ResponseEntity.ok(usuarioActualizado);
    }

    @DeleteMapping({"/{id}", "/{id}/"})
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
