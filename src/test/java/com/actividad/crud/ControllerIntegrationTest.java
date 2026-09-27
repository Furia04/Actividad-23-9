package com.actividad.crud;

import com.actividad.crud.dto.ReservaDTO;
import com.actividad.crud.dto.UsuarioDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static Long usuarioIdCreado;
    private static Long reservaIdCreada;

    @Test
    @Order(1)
    void testCrearUsuario_201() throws Exception {
        UsuarioDTO dto = UsuarioDTO.builder()
                .nombre("Maria Lopez")
                .correo("maria.lopez@example.com")
                .edad(30)
                .build();

        String response = mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nombre").value("Maria Lopez"))
                .andExpect(jsonPath("$.correo").value("maria.lopez@example.com"))
                .andExpect(jsonPath("$.edad").value(30))
                .andReturn().getResponse().getContentAsString();

        usuarioIdCreado = objectMapper.readTree(response).get("id").asLong();
    }

    @Test
    @Order(2)
    void testListarUsuarios_200() throws Exception {
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[?(@.correo == 'maria.lopez@example.com')]").exists());

        // Probar también con barra al final: /usuarios/
        mockMvc.perform(get("/usuarios/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @Order(3)
    void testBuscarUsuarioPorId_200() throws Exception {
        mockMvc.perform(get("/usuarios/{id}", usuarioIdCreado))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuarioIdCreado))
                .andExpect(jsonPath("$.nombre").value("Maria Lopez"));
    }

    @Test
    @Order(4)
    void testModificarUsuario_200() throws Exception {
        UsuarioDTO updateDto = UsuarioDTO.builder()
                .nombre("Maria Fernanda Lopez")
                .correo("maria.fernanda@example.com")
                .edad(31)
                .build();

        mockMvc.perform(put("/usuarios/{id}", usuarioIdCreado)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Maria Fernanda Lopez"))
                .andExpect(jsonPath("$.correo").value("maria.fernanda@example.com"))
                .andExpect(jsonPath("$.edad").value(31));
    }

    @Test
    @Order(5)
    void testCrearReserva_201() throws Exception {
        ReservaDTO dto = ReservaDTO.builder()
                .fechaHora(LocalDateTime.of(2026, 11, 20, 20, 0, 0))
                .cantidadPersonas(2)
                .observaciones("Aniversario, mesa tranquila")
                .usuarioId(usuarioIdCreado)
                .build();

        String response = mockMvc.perform(post("/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.cantidadPersonas").value(2))
                .andExpect(jsonPath("$.usuarioId").value(usuarioIdCreado))
                .andExpect(jsonPath("$.observaciones").value("Aniversario, mesa tranquila"))
                .andReturn().getResponse().getContentAsString();

        reservaIdCreada = objectMapper.readTree(response).get("id").asLong();
    }

    @Test
    @Order(6)
    void testListarReservas_200() throws Exception {
        mockMvc.perform(get("/reservas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[?(@.id == " + reservaIdCreada + ")]").exists());

        // Probar también con barra al final: /reservas/
        mockMvc.perform(get("/reservas/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @Order(7)
    void testBuscarReservaPorId_200() throws Exception {
        mockMvc.perform(get("/reservas/{id}", reservaIdCreada))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reservaIdCreada))
                .andExpect(jsonPath("$.cantidadPersonas").value(2))
                .andExpect(jsonPath("$.usuarioId").value(usuarioIdCreado));
    }

    @Test
    @Order(8)
    void testModificarReserva_200() throws Exception {
        ReservaDTO updateDto = ReservaDTO.builder()
                .fechaHora(LocalDateTime.of(2026, 11, 20, 21, 30, 0))
                .cantidadPersonas(3)
                .observaciones("Se suma 1 invitado más")
                .usuarioId(usuarioIdCreado)
                .build();

        mockMvc.perform(put("/reservas/{id}", reservaIdCreada)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidadPersonas").value(3))
                .andExpect(jsonPath("$.observaciones").value("Se suma 1 invitado más"));
    }

    @Test
    @Order(9)
    void testExcepcionPropia_UsuarioNoEncontrado_404() throws Exception {
        mockMvc.perform(get("/usuarios/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Usuario no encontrado"))
                .andExpect(jsonPath("$.mensaje").value("Usuario no encontrado con ID: 999999"));
    }

    @Test
    @Order(10)
    void testExcepcionPropia_ReservaNoEncontrada_404() throws Exception {
        mockMvc.perform(get("/reservas/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Reserva no encontrada"))
                .andExpect(jsonPath("$.mensaje").value("Reserva no encontrada con ID: 999999"));
    }

    @Test
    @Order(11)
    void testEliminarReserva_204() throws Exception {
        mockMvc.perform(delete("/reservas/{id}", reservaIdCreada))
                .andExpect(status().isNoContent());

        // Verificar que ya no existe (lanza 404)
        mockMvc.perform(get("/reservas/{id}", reservaIdCreada))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(12)
    void testEliminarUsuario_204() throws Exception {
        mockMvc.perform(delete("/usuarios/{id}", usuarioIdCreado))
                .andExpect(status().isNoContent());

        // Verificar que ya no existe (lanza 404)
        mockMvc.perform(get("/usuarios/{id}", usuarioIdCreado))
                .andExpect(status().isNotFound());
    }
}
