package com.actividad.crud;

import com.actividad.crud.dto.ReservaDTO;
import com.actividad.crud.dto.UsuarioDTO;
import com.actividad.crud.dto.UsuarioRegistroDTO;
import com.actividad.crud.model.Usuario;
import com.actividad.crud.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    @Autowired
    private UsuarioRepository usuarioRepository;

    private static Long usuarioIdCreado;
    private static Long reservaIdCreada;

    private String basicAuthHeader(String username, String password) {
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedAuth, StandardCharsets.UTF_8);
    }

    // 1. Registrar un usuario válido sin autenticación -> 201; respuesta sin contraseña ni hash
    @Test
    @Order(1)
    void caso1_registrarUsuarioValidoSinAutenticacion_201() throws Exception {
        UsuarioRegistroDTO dto = UsuarioRegistroDTO.builder()
                .nombre("Maria Lopez")
                .correo("maria.lopez@example.com")
                .password("ClaveSegura123")
                .edad(30)
                .build();

        String response = mockMvc.perform(post("/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nombre").value("Maria Lopez"))
                .andExpect(jsonPath("$.correo").value("maria.lopez@example.com"))
                .andExpect(jsonPath("$.edad").value(30))
                .andExpect(jsonPath("$.rol").value("USER"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist())
                .andReturn().getResponse().getContentAsString();

        usuarioIdCreado = objectMapper.readTree(response).get("id").asLong();
    }

    // 2. Repetir el correo de un usuario registrado -> 409; no se crea otro usuario
    @Test
    @Order(2)
    void caso2_repetirCorreoUsuarioRegistrado_409() throws Exception {
        UsuarioRegistroDTO dtoRepetido = UsuarioRegistroDTO.builder()
                .nombre("Maria Clon")
                .correo("maria.lopez@example.com")
                .password("OtraClave123")
                .edad(28)
                .build();

        mockMvc.perform(post("/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoRepetido)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.mensaje").value(containsString("ya se encuentra registrado")));
    }

    // 3. Registrar datos inválidos -> 400; no se guardan (contraseña < 6 caracteres y correo inválido)
    @Test
    @Order(3)
    void caso3_registrarDatosInvalidos_400() throws Exception {
        UsuarioRegistroDTO dtoInvalido = UsuarioRegistroDTO.builder()
                .nombre("")
                .correo("formato-invalido")
                .password("123") // Menos de 6 caracteres
                .edad(-5)
                .build();

        mockMvc.perform(post("/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detalles").isArray());
    }

    // 4. Acceder a una ruta protegida sin credenciales o con contraseña incorrecta -> 401
    @Test
    @Order(4)
    void caso4_accederRutaProtegidaSinCredencialesOIncorrectas_401() throws Exception {
        // Sin credenciales
        mockMvc.perform(get("/reservas"))
                .andExpect(status().isUnauthorized());

        // Con contraseña incorrecta
        mockMvc.perform(get("/reservas")
                        .header(HttpHeaders.AUTHORIZATION, basicAuthHeader("user@ucc.edu.ar", "PasswordErronea")))
                .andExpect(status().isUnauthorized());
    }

    // 5. Acceder con correo y contraseña correctos -> Se permite la operación (200 OK)
    @Test
    @Order(5)
    void caso5_accederConCredencialesCorrectas_200() throws Exception {
        mockMvc.perform(get("/reservas")
                        .header(HttpHeaders.AUTHORIZATION, basicAuthHeader("user@ucc.edu.ar", "User123!")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // 6. Realizar el CRUD y una consulta por relación -> Funcionalidades de la primera entrega conservadas
    @Test
    @Order(6)
    void caso6_crudYConsultaPorRelacionConservados() throws Exception {
        String authUser = basicAuthHeader("user@ucc.edu.ar", "User123!");

        // Listar usuarios
        mockMvc.perform(get("/usuarios").header(HttpHeaders.AUTHORIZATION, authUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(1)));

        // Buscar usuario por ID
        mockMvc.perform(get("/usuarios/{id}", usuarioIdCreado).header(HttpHeaders.AUTHORIZATION, authUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuarioIdCreado));

        // Modificar usuario
        UsuarioDTO modDto = UsuarioDTO.builder()
                .nombre("Maria Actualizada")
                .correo("maria.actualizada@example.com")
                .edad(31)
                .build();
        mockMvc.perform(put("/usuarios/{id}", usuarioIdCreado)
                        .header(HttpHeaders.AUTHORIZATION, authUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Maria Actualizada"));

        // Crear reserva
        ReservaDTO reservaDto = ReservaDTO.builder()
                .fechaHora(LocalDateTime.of(2026, 11, 20, 20, 0, 0))
                .cantidadPersonas(2)
                .observaciones("Cena de trabajo")
                .usuarioId(usuarioIdCreado)
                .build();

        String resResponse = mockMvc.perform(post("/reservas")
                        .header(HttpHeaders.AUTHORIZATION, authUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservaDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();

        reservaIdCreada = objectMapper.readTree(resResponse).get("id").asLong();

        // Consulta que utiliza la relación JPA: GET /reservas/usuario/{usuarioId}
        mockMvc.perform(get("/reservas/usuario/{usuarioId}", usuarioIdCreado)
                        .header(HttpHeaders.AUTHORIZATION, authUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$[0].usuarioId").value(usuarioIdCreado))
                .andExpect(jsonPath("$[0].usuario.id").value(usuarioIdCreado))
                .andExpect(jsonPath("$[0].usuario.password").doesNotExist());
    }

    // 7. Revisar el usuario guardado y sus respuestas -> Hash BCrypt en la base; ninguna respuesta expone contraseña ni hash
    @Test
    @Order(7)
    void caso7_revisarUsuarioGuardadoHashBCryptYSinExposicion() throws Exception {
        Usuario usuario = usuarioRepository.findById(usuarioIdCreado).orElseThrow();

        // Verificar que en base de datos está hasheada con BCrypt
        assertTrue(usuario.getPassword().startsWith("$2a$") || usuario.getPassword().startsWith("$2b$"));
        assertFalse(usuario.getPassword().contains("ClaveSegura123"));

        // Verificar que ningún endpoint expone la contraseña
        mockMvc.perform(get("/usuarios/{id}", usuarioIdCreado)
                        .header(HttpHeaders.AUTHORIZATION, basicAuthHeader("user@ucc.edu.ar", "User123!")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    // 8. Acceder con credenciales válidas pero sin el rol requerido -> 403 Forbidden (USER intenta eliminar)
    @Test
    @Order(8)
    void caso8_accederSinRolRequerido_403() throws Exception {
        mockMvc.perform(delete("/usuarios/{id}", usuarioIdCreado)
                        .header(HttpHeaders.AUTHORIZATION, basicAuthHeader("user@ucc.edu.ar", "User123!")))
                .andExpect(status().isForbidden());
    }

    // 9. Acceder a una operación restringida con el rol correcto -> Operación permitida (ADMIN elimina)
    @Test
    @Order(9)
    void caso9_accederConRolCorrectoAdmin_204() throws Exception {
        // Eliminar reserva primero para mantener integridad referencial
        mockMvc.perform(delete("/reservas/{id}", reservaIdCreada)
                        .header(HttpHeaders.AUTHORIZATION, basicAuthHeader("admin@ucc.edu.ar", "Admin123!")))
                .andExpect(status().isNoContent());

        // ADMIN elimina usuario
        mockMvc.perform(delete("/usuarios/{id}", usuarioIdCreado)
                        .header(HttpHeaders.AUTHORIZATION, basicAuthHeader("admin@ucc.edu.ar", "Admin123!")))
                .andExpect(status().isNoContent());

        // Verificar que ya no existe (404)
        mockMvc.perform(get("/usuarios/{id}", usuarioIdCreado)
                        .header(HttpHeaders.AUTHORIZATION, basicAuthHeader("admin@ucc.edu.ar", "Admin123!")))
                .andExpect(status().isNotFound());
    }
}
