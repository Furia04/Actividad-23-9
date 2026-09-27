package com.actividad.crud.controller;

import com.actividad.crud.dto.UsuarioDTO;
import com.actividad.crud.model.Usuario;
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
    public ResponseEntity<List<Usuario>> listar() {
        return ResponseEntity.ok(usuarioService.listar());
    }

    @GetMapping({"/{id}", "/{id}/"})
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Usuario> crear(@Valid @RequestBody UsuarioDTO dto) {
        Usuario nuevoUsuario = usuarioService.crear(dto);
        return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
    }

    @PutMapping({"/{id}", "/{id}/"})
    public ResponseEntity<Usuario> modificar(@PathVariable Long id, @Valid @RequestBody UsuarioDTO dto) {
        Usuario usuarioActualizado = usuarioService.modificar(id, dto);
        return ResponseEntity.ok(usuarioActualizado);
    }

    @DeleteMapping({"/{id}", "/{id}/"})
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
