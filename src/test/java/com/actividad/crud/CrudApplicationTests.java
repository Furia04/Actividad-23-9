package com.actividad.crud;

import com.actividad.crud.dto.ReservaDTO;
import com.actividad.crud.dto.UsuarioDTO;
import com.actividad.crud.exception.ReservaNoEncontradaException;
import com.actividad.crud.exception.UsuarioNoEncontradoException;
import com.actividad.crud.model.Reserva;
import com.actividad.crud.model.Usuario;
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

    @Test
    void testCrudCompletoUsuarioYReserva() {
        // 1. Crear Usuario
        UsuarioDTO userDto = UsuarioDTO.builder()
                .nombre("Carlos Rodriguez")
                .correo("carlos@mail.com")
                .edad(28)
                .build();
        Usuario usuarioCreado = usuarioService.crear(userDto);
        assertNotNull(usuarioCreado.getId());
        assertEquals("Carlos Rodriguez", usuarioCreado.getNombre());

        // 2. Listar y Buscar Usuario por ID
        List<Usuario> usuarios = usuarioService.listar();
        assertFalse(usuarios.isEmpty());

        Usuario usuarioEncontrado = usuarioService.buscarPorId(usuarioCreado.getId());
        assertEquals("carlos@mail.com", usuarioEncontrado.getCorreo());

        // 3. Modificar Usuario
        UsuarioDTO userUpdateDto = UsuarioDTO.builder()
                .nombre("Carlos R. Rodriguez")
                .correo("carlos.nuevo@mail.com")
                .edad(29)
                .build();
        Usuario usuarioModificado = usuarioService.modificar(usuarioCreado.getId(), userUpdateDto);
        assertEquals("Carlos R. Rodriguez", usuarioModificado.getNombre());
        assertEquals("carlos.nuevo@mail.com", usuarioModificado.getCorreo());

        // 4. Crear Reserva para este Usuario
        ReservaDTO reservaDto = ReservaDTO.builder()
                .fechaHora(LocalDateTime.of(2026, 10, 15, 21, 0, 0))
                .cantidadPersonas(4)
                .observaciones("Mesa exterior")
                .usuarioId(usuarioCreado.getId())
                .build();
        Reserva reservaCreada = reservaService.crear(reservaDto);
        assertNotNull(reservaCreada.getId());
        assertEquals(4, reservaCreada.getCantidadPersonas());
        assertEquals(usuarioCreado.getId(), reservaCreada.getUsuarioId());

        // 5. Listar y Buscar Reserva por ID
        List<Reserva> reservas = reservaService.listar();
        assertFalse(reservas.isEmpty());

        Reserva reservaEncontrada = reservaService.buscarPorId(reservaCreada.getId());
        assertEquals("Mesa exterior", reservaEncontrada.getObservaciones());

        // 6. Modificar Reserva
        ReservaDTO reservaUpdateDto = ReservaDTO.builder()
                .fechaHora(LocalDateTime.of(2026, 10, 15, 21, 30, 0))
                .cantidadPersonas(5)
                .observaciones("Mesa interior salón principal")
                .usuarioId(usuarioCreado.getId())
                .build();
        Reserva reservaModificada = reservaService.modificar(reservaCreada.getId(), reservaUpdateDto);
        assertEquals(5, reservaModificada.getCantidadPersonas());
        assertEquals("Mesa interior salón principal", reservaModificada.getObservaciones());

        // 7. Probar Excepción Propia al buscar IDs inexistentes
        assertThrows(UsuarioNoEncontradoException.class, () -> usuarioService.buscarPorId(999999L));
        assertThrows(ReservaNoEncontradaException.class, () -> reservaService.buscarPorId(999999L));

        // 8. Eliminar Reserva y verificar
        Long idReserva = reservaCreada.getId();
        reservaService.eliminar(idReserva);
        assertThrows(ReservaNoEncontradaException.class, () -> reservaService.buscarPorId(idReserva));

        // 9. Eliminar Usuario y verificar
        Long idUsuario = usuarioCreado.getId();
        usuarioService.eliminar(idUsuario);
        assertThrows(UsuarioNoEncontradoException.class, () -> usuarioService.buscarPorId(idUsuario));
    }
}
