package com.actividad.crud;

import com.actividad.crud.dto.ReservaDTO;
import com.actividad.crud.dto.ReservaResponseDTO;
import com.actividad.crud.dto.UsuarioDTO;
import com.actividad.crud.dto.UsuarioRegistroDTO;
import com.actividad.crud.dto.UsuarioResponseDTO;
import com.actividad.crud.exception.CorreoYaExisteException;
import com.actividad.crud.exception.ReservaNoEncontradaException;
import com.actividad.crud.exception.UsuarioNoEncontradoException;
import com.actividad.crud.model.Usuario;
import com.actividad.crud.repository.UsuarioRepository;
import com.actividad.crud.service.ReservaService;
import com.actividad.crud.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CrudApplicationTests {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void testCrudCompletoYSeguridadUsuarioYReserva() {
        // 1. Registrar Usuario con contraseña
        UsuarioRegistroDTO userDto = UsuarioRegistroDTO.builder()
                .nombre("Carlos Rodriguez")
                .correo("carlos.test@mail.com")
                .password("Password123!")
                .edad(28)
                .build();
        UsuarioResponseDTO usuarioCreado = usuarioService.registrar(userDto);
        assertNotNull(usuarioCreado.getId());
        assertEquals("Carlos Rodriguez", usuarioCreado.getNombre());
        assertEquals("carlos.test@mail.com", usuarioCreado.getCorreo());

        // Verificar que en la base de datos la contraseña está hasheada con BCrypt
        Usuario entidadEnDb = usuarioRepository.findById(usuarioCreado.getId()).orElseThrow();
        assertNotEquals("Password123!", entidadEnDb.getPassword());
        assertTrue(entidadEnDb.getPassword().startsWith("$2a$") || entidadEnDb.getPassword().startsWith("$2b$"));

        // 2. Verificar que el correo repetido lanza CorreoYaExisteException (409)
        assertThrows(CorreoYaExisteException.class, () -> usuarioService.registrar(userDto));

        // 3. Listar y Buscar Usuario por ID
        List<UsuarioResponseDTO> usuarios = usuarioService.listar();
        assertFalse(usuarios.isEmpty());

        UsuarioResponseDTO usuarioEncontrado = usuarioService.buscarPorId(usuarioCreado.getId());
        assertEquals("carlos.test@mail.com", usuarioEncontrado.getCorreo());

        // 4. Modificar Usuario
        UsuarioDTO userUpdateDto = UsuarioDTO.builder()
                .nombre("Carlos R. Rodriguez")
                .correo("carlos.nuevo@mail.com")
                .edad(29)
                .build();
        UsuarioResponseDTO usuarioModificado = usuarioService.modificar(usuarioCreado.getId(), userUpdateDto);
        assertEquals("Carlos R. Rodriguez", usuarioModificado.getNombre());
        assertEquals("carlos.nuevo@mail.com", usuarioModificado.getCorreo());

        // 5. Crear Reserva para este Usuario
        ReservaDTO reservaDto = ReservaDTO.builder()
                .fechaHora(LocalDateTime.of(2026, 10, 15, 21, 0, 0))
                .cantidadPersonas(4)
                .observaciones("Mesa exterior")
                .usuarioId(usuarioCreado.getId())
                .build();
        ReservaResponseDTO reservaCreada = reservaService.crear(reservaDto);
        assertNotNull(reservaCreada.getId());
        assertEquals(4, reservaCreada.getCantidadPersonas());
        assertEquals(usuarioCreado.getId(), reservaCreada.getUsuarioId());

        // 6. Consulta por relación JPA: reservas del usuario
        List<ReservaResponseDTO> reservasDelUsuario = reservaService.buscarPorUsuarioId(usuarioCreado.getId());
        assertFalse(reservasDelUsuario.isEmpty());
        assertEquals(1, reservasDelUsuario.size());

        // 7. Listar y Buscar Reserva por ID
        List<ReservaResponseDTO> reservas = reservaService.listar();
        assertFalse(reservas.isEmpty());

        ReservaResponseDTO reservaEncontrada = reservaService.buscarPorId(reservaCreada.getId());
        assertEquals("Mesa exterior", reservaEncontrada.getObservaciones());

        // 8. Modificar Reserva
        ReservaDTO reservaUpdateDto = ReservaDTO.builder()
                .fechaHora(LocalDateTime.of(2026, 10, 15, 21, 30, 0))
                .cantidadPersonas(5)
                .observaciones("Mesa interior salón principal")
                .usuarioId(usuarioCreado.getId())
                .build();
        ReservaResponseDTO reservaModificada = reservaService.modificar(reservaCreada.getId(), reservaUpdateDto);
        assertEquals(5, reservaModificada.getCantidadPersonas());
        assertEquals("Mesa interior salón principal", reservaModificada.getObservaciones());

        // 9. Excepciones de no encontrado
        assertThrows(UsuarioNoEncontradoException.class, () -> usuarioService.buscarPorId(999999L));
        assertThrows(ReservaNoEncontradaException.class, () -> reservaService.buscarPorId(999999L));

        // 10. Eliminar Reserva
        Long idReserva = reservaCreada.getId();
        reservaService.eliminar(idReserva);
        assertThrows(ReservaNoEncontradaException.class, () -> reservaService.buscarPorId(idReserva));

        // 11. Eliminar Usuario
        Long idUsuario = usuarioCreado.getId();
        usuarioService.eliminar(idUsuario);
        assertThrows(UsuarioNoEncontradoException.class, () -> usuarioService.buscarPorId(idUsuario));
    }
}
