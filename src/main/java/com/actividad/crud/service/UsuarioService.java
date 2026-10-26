package com.actividad.crud.service;

import com.actividad.crud.dto.UsuarioDTO;
import com.actividad.crud.dto.UsuarioRegistroDTO;
import com.actividad.crud.dto.UsuarioResponseDTO;
import com.actividad.crud.exception.CorreoYaExisteException;
import com.actividad.crud.exception.UsuarioNoEncontradoException;
import com.actividad.crud.exception.ValidacionException;
import com.actividad.crud.mapper.UsuarioMapper;
import com.actividad.crud.model.Rol;
import com.actividad.crud.model.Usuario;
import com.actividad.crud.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponseDTO registrar(UsuarioRegistroDTO dto) {
        log.info("Iniciando registro de usuario con correo: {}", dto.getCorreo());

        if (dto.getNombre() == null || dto.getNombre().trim().length() < 2) {
            throw new ValidacionException("El nombre debe tener al menos 2 caracteres");
        }

        if (dto.getPassword() == null || dto.getPassword().trim().length() < 6) {
            throw new ValidacionException("La contraseña debe tener al menos 6 caracteres");
        }

        String correoLimpio = dto.getCorreo().trim().toLowerCase();
        if (usuarioRepository.existsByCorreo(correoLimpio)) {
            throw new CorreoYaExisteException(correoLimpio);
        }

        String passwordHash = passwordEncoder.encode(dto.getPassword().trim());

        Usuario usuario = Usuario.builder()
                .nombre(dto.getNombre().trim())
                .correo(correoLimpio)
                .edad(dto.getEdad())
                .password(passwordHash)
                .rol(Rol.USER) // El registro público siempre asigna el rol básico USER
                .build();

        Usuario guardado = usuarioRepository.save(usuario);
        log.info("Usuario registrado exitosamente con ID: {}", guardado.getId());
        return usuarioMapper.toResponseDTO(guardado);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listar() {
        log.info("Consultando todos los usuarios");
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id) {
        log.info("Buscando usuario con id: {}", id);
        Usuario usuario = buscarEntidadPorId(id);
        return usuarioMapper.toResponseDTO(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario buscarEntidadPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(id));
    }

    @Transactional
    public UsuarioResponseDTO modificar(Long id, UsuarioDTO dto) {
        log.info("Modificando usuario con id: {}", id);

        Usuario usuarioExistente = buscarEntidadPorId(id);

        if (dto.getNombre() != null && dto.getNombre().trim().length() < 2) {
            throw new ValidacionException("El nombre debe tener al menos 2 caracteres");
        }

        if (dto.getCorreo() != null && !dto.getCorreo().trim().equalsIgnoreCase(usuarioExistente.getCorreo())) {
            String nuevoCorreo = dto.getCorreo().trim().toLowerCase();
            if (usuarioRepository.existsByCorreo(nuevoCorreo)) {
                throw new CorreoYaExisteException(nuevoCorreo);
            }
            usuarioExistente.setCorreo(nuevoCorreo);
        }

        usuarioMapper.updateEntityFromDTO(usuarioExistente, dto);
        Usuario actualizado = usuarioRepository.save(usuarioExistente);
        return usuarioMapper.toResponseDTO(actualizado);
    }

    @Transactional
    public void eliminar(Long id) {
        log.info("Eliminando usuario con id: {}", id);
        Usuario usuarioExistente = buscarEntidadPorId(id);
        usuarioRepository.delete(usuarioExistente);
    }
}
