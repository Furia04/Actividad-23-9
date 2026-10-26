package com.actividad.crud.config;

import com.actividad.crud.model.Reserva;
import com.actividad.crud.model.Rol;
import com.actividad.crud.model.Usuario;
import com.actividad.crud.repository.ReservaRepository;
import com.actividad.crud.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final ReservaRepository reservaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("Inicializando datos de prueba para el 2do Entregable...");

        // 1. Administrador de prueba
        Usuario admin = usuarioRepository.findByCorreo("admin@ucc.edu.ar").orElseGet(() -> {
            log.info("Creando usuario ADMIN de prueba: admin@ucc.edu.ar");
            Usuario nuevoAdmin = Usuario.builder()
                    .nombre("Administrador UCC")
                    .correo("admin@ucc.edu.ar")
                    .password(passwordEncoder.encode("Admin123!"))
                    .edad(35)
                    .rol(Rol.ADMIN)
                    .build();
            return usuarioRepository.save(nuevoAdmin);
        });

        // 2. Usuario regular de prueba
        Usuario user = usuarioRepository.findByCorreo("user@ucc.edu.ar").orElseGet(() -> {
            log.info("Creando usuario USER de prueba: user@ucc.edu.ar");
            Usuario nuevoUser = Usuario.builder()
                    .nombre("Usuario Prueba")
                    .correo("user@ucc.edu.ar")
                    .password(passwordEncoder.encode("User123!"))
                    .edad(24)
                    .rol(Rol.USER)
                    .build();
            return usuarioRepository.save(nuevoUser);
        });

        // 3. Reserva de prueba inicial si la base está vacía
        if (reservaRepository.count() == 0) {
            log.info("Creando reserva inicial para el usuario de prueba");
            Reserva reservaInicial = Reserva.builder()
                    .fechaHora(LocalDateTime.now().plusDays(2).withHour(20).withMinute(0).withSecond(0).withNano(0))
                    .cantidadPersonas(2)
                    .observaciones("Cena de bienvenida - Mesa 4")
                    .usuario(user)
                    .build();
            reservaRepository.save(reservaInicial);
        }

        log.info("Inicialización de datos completada exitosamente.");
    }
}
