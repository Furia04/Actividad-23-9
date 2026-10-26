package com.actividad.crud.controller;

import com.actividad.crud.dto.UsuarioRegistroDTO;
import com.actividad.crud.dto.UsuarioResponseDTO;
import com.actividad.crud.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/auth", "/auth/"})
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;

    @PostMapping({"/registro", "/registro/"})
    public ResponseEntity<UsuarioResponseDTO> registrar(@Valid @RequestBody UsuarioRegistroDTO dto) {
        UsuarioResponseDTO nuevoUsuario = usuarioService.registrar(dto);
        return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
    }
}
