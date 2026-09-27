package com.actividad.crud.service;

import com.actividad.crud.dto.UsuarioDTO;
import com.actividad.crud.exception.UsuarioNoEncontradoException;
import com.actividad.crud.exception.ValidacionException;
import com.actividad.crud.model.Usuario;
import com.actividad.crud.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<Usuario> listar() {
        log.info("Consultando todos los usuarios");
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        log.info("Buscando usuario con id: {}", id);
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(id));
    }

    @Transactional
    public Usuario crear(UsuarioDTO dto) {
        log.info("Creando usuario con correo: {}", dto.getCorreo());

        if (dto.getNombre() == null || dto.getNombre().trim().length() < 2) {
            throw new ValidacionException("El nombre debe tener al menos 2 caracteres");
        }

        Usuario usuario = Usuario.builder()
                .nombre(dto.getNombre().trim())
                .correo(dto.getCorreo().trim())
                .edad(dto.getEdad())
                .build();

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario modificar(Long id, UsuarioDTO dto) {
        log.info("Modificando usuario con id: {}", id);

        Usuario usuarioExistente = buscarPorId(id);

        if (dto.getNombre() == null || dto.getNombre().trim().length() < 2) {
            throw new ValidacionException("El nombre debe tener al menos 2 caracteres");
        }

        usuarioExistente.setNombre(dto.getNombre().trim());
        usuarioExistente.setCorreo(dto.getCorreo().trim());
        usuarioExistente.setEdad(dto.getEdad());

        return usuarioRepository.save(usuarioExistente);
    }

    @Transactional
    public void eliminar(Long id) {
        log.info("Eliminando usuario con id: {}", id);
        Usuario usuarioExistente = buscarPorId(id);
        usuarioRepository.delete(usuarioExistente);
    }
}
