# Segundo Entregable - CRUD de Usuarios y Reservas con Spring Security y RBAC

Trabajo práctico de Laboratorio de Programación 2 (TUDS - Universidad Católica de Córdoba).  
API REST desarrollada con **Spring Boot 3**, **Spring Security** y **Spring Data JPA** para gestionar Usuarios y Reservas gastronómicas, incorporando autenticación HTTP Basic (stateless), control de acceso basado en roles (RBAC), encriptación con BCrypt, capas DTO/Mapper para evitar la exposición de credenciales y consultas por relación JPA.

---

## 1. Descripción del Sistema

El sistema implementa una API RESTful para la administración de usuarios y reservas gastronómicas:
- **Usuarios**: Clientes o administradores del sistema, identificados de forma unívoca por su correo electrónico.
- **Reservas**: Turnos reservados que registran fecha y hora, cantidad de comensales, observaciones y el usuario asociado (relación `@ManyToOne`).
- **Seguridad**: Autenticación HTTP Basic con sesión Stateless. Las contraseñas se almacenan cifradas con BCrypt y nunca son expuestas en respuestas JSON. Se aplica autorización basada en roles (`USER` y `ADMIN`).

---

## 2. Cambios Introducidos en la Segunda Entrega

Respecto a la primera entrega, se implementaron las siguientes mejoras arquitectónicas y de seguridad:

1. **Spring Security & HTTP Basic Stateless**:
   - Integración de `spring-boot-starter-security`.
   - Política de sesión `SessionCreationPolicy.STATELESS` (sin sesiones HTTP de servidor).
   - `CustomUserDetailsService` para autenticar usuarios desde la base de datos por correo electrónico.
2. **Cifrado de Contraseñas con BCrypt**:
   - Configuración de `PasswordEncoder` con `BCryptPasswordEncoder`.
   - Las contraseñas se encriptan al registrarse y se almacenan con su hash seguro (`$2a$...`).
3. **Control de Acceso Basado en Roles (RBAC)**:
   - Se incorporó el enumerador `Rol` con valores `USER` y `ADMIN`.
   - Operaciones restringidas: La eliminación de entidades (`DELETE /usuarios/**` y `DELETE /reservas/**`) está restringida exclusivamente a usuarios con rol `ADMIN`.
   - Rutas públicas: Registro de usuarios (`/auth/registro` y `/usuarios/registro`) y consola H2 (`/h2-console/**`).
   - Rutas protegidas: Cualquier otra operación CRUD requiere autenticación válida.
4. **Separación de Capas con DTOs y Mappers**:
   - Creación de `UsuarioRegistroDTO` (valida correo único, formato y contraseña $\ge$ 6 caracteres).
   - Creación de `UsuarioResponseDTO` (contiene `id`, `nombre`, `correo`, `edad`, `rol`; **nunca expone la contraseña ni su hash**).
   - Creación de `ReservaResponseDTO` (contiene la información de la reserva y el `UsuarioResponseDTO` asociado).
   - Mappers dedicados (`UsuarioMapper` y `ReservaMapper`) para desacoplar el modelo de base de datos de la API.
5. **Consulta por Relación JPA**:
   - Se añadió el endpoint `GET /reservas/usuario/{usuarioId}` para obtener todas las reservas asociadas a un usuario específico mediante consulta derivada en `ReservaRepository`.
6. **Manejo Global de Excepciones HTTP**:
   - `409 Conflict`: `CorreoYaExisteException` y `DataIntegrityViolationException` al intentar registrar correos duplicados.
   - `400 Bad Request`: `MethodArgumentNotValidException` para errores de validación de campos.
   - `401 Unauthorized`: Acceso sin credenciales o con contraseña incorrecta.
   - `403 Forbidden`: `AccessDeniedException` al intentar ejecutar acciones sin los permisos del rol.
   - `404 Not Found`: `UsuarioNoEncontradoException` y `ReservaNoEncontradaException`.
7. **Población Inicial Automática**:
   - `DataInitializer` crea al iniciar la aplicación los usuarios de prueba (`admin@ucc.edu.ar` y `user@ucc.edu.ar`) y reservas de ejemplo con contraseñas ya hasheadas.

---

## 3. Datos de Prueba (Credenciales)

Al iniciar la aplicación, la base de datos se inicializa automáticamente con los siguientes usuarios de prueba:

| Rol | Correo Electrónico | Contraseña | Permisos |
|---|---|---|---|
| **ADMIN** | `admin@ucc.edu.ar` | `Admin123!` | Acceso completo a todas las operaciones CRUD (incluyendo `DELETE`). |
| **USER** | `user@ucc.edu.ar` | `User123!` | Operaciones de lectura y creación/modificación. Denegado en `DELETE` (403 Forbidden). |

*(También es posible registrar nuevos usuarios libremente a través de `POST /auth/registro` sin necesidad de estar autenticado).*

---

## 4. Tabla de Endpoints y Permisos

| Método | Endpoint | Permiso / Rol Requerido | Descripción | Código Éxito | Códigos Error Comunes |
|---|---|---|---|---|---|
| **POST** | `/auth/registro` | **Público** (Sin autenticación) | Registro de un nuevo usuario en el sistema. | `201 Created` | `400 Bad Request`, `409 Conflict` |
| **POST** | `/usuarios/registro` | **Público** (Sin autenticación) | Alias de registro público en el controlador de usuarios. | `201 Created` | `400 Bad Request`, `409 Conflict` |
| **GET** | `/usuarios` | Autenticado (`USER` o `ADMIN`) | Listado de todos los usuarios registrados (sin hashes). | `200 OK` | `401 Unauthorized` |
| **GET** | `/usuarios/{id}` | Autenticado (`USER` o `ADMIN`) | Consulta de usuario por ID. | `200 OK` | `401 Unauthorized`, `404 Not Found` |
| **PUT** | `/usuarios/{id}` | Autenticado (`USER` o `ADMIN`) | Modificación de datos del usuario. | `200 OK` | `400 Bad Request`, `401 Unauthorized`, `404 Not Found`, `409 Conflict` |
| **DELETE** | `/usuarios/{id}` | **Solo ADMIN** | Eliminación física del usuario. | `204 No Content` | `401 Unauthorized`, `403 Forbidden`, `404 Not Found` |
| **GET** | `/reservas` | Autenticado (`USER` o `ADMIN`) | Listado de todas las reservas. | `200 OK` | `401 Unauthorized` |
| **GET** | `/reservas/{id}` | Autenticado (`USER` o `ADMIN`) | Consulta de reserva por ID con usuario anidado. | `200 OK` | `401 Unauthorized`, `404 Not Found` |
| **GET** | `/reservas/usuario/{usuarioId}` | Autenticado (`USER` o `ADMIN`) | **Consulta por relación**: Reservas del usuario indicado. | `200 OK` | `401 Unauthorized`, `404 Not Found` |
| **POST** | `/reservas` | Autenticado (`USER` o `ADMIN`) | Creación de nueva reserva vinculada a un usuario. | `201 Created` | `400 Bad Request`, `401 Unauthorized`, `404 Not Found` |
| **PUT** | `/reservas/{id}` | Autenticado (`USER` o `ADMIN`) | Modificación de una reserva existente. | `200 OK` | `400 Bad Request`, `401 Unauthorized`, `404 Not Found` |
| **DELETE** | `/reservas/{id}` | **Solo ADMIN** | Eliminación física de una reserva. | `204 No Content` | `401 Unauthorized`, `403 Forbidden`, `404 Not Found` |
| **ALL** | `/h2-console/**` | **Público** | Consola web de base de datos H2. | `200 OK` | - |

---

## 5. Guía para Ejecutar las Pruebas desde Postman

El repositorio incluye el archivo `CRUD_Usuarios_Reservas.postman_collection.json`, preparado con los 9 escenarios de prueba requeridos por la consigna:

### Pasos:
1. Abrir **Postman** e importar el archivo `CRUD_Usuarios_Reservas.postman_collection.json`.
2. Verificar que la variable de colección `baseUrl` esté configurada en `http://localhost:8080`.
3. Iniciar el servidor Spring Boot (ver sección *Ejecución del Proyecto*).
4. Ejecutar las peticiones organizadas en las carpetas de la colección:
   - **1. Registro válido (201 Created)**: Petición `POST /auth/registro` con datos correctos y clave $\ge$ 6 caracteres. Verificar que devuelve `201` y que el JSON de respuesta **no contiene el campo `password` ni su hash**.
   - **2. Correo duplicado (409 Conflict)**: Reintentar el envío de la misma petición anterior. Verificar que devuelve `409 Conflict`.
   - **3. Datos inválidos (400 Bad Request)**: Petición `POST /auth/registro` con nombre vacío, correo inválido y clave menor a 6 caracteres. Verificar que devuelve `400` y detalla los errores de validación.
   - **4. Acceso sin credenciales (401 Unauthorized)**: Petición `GET /reservas` sin cabecera de autorización.
   - **5. Acceso con credenciales correctas (200 OK)**: Petición `GET /reservas` con Basic Auth (`user@ucc.edu.ar` / `User123!`).
   - **6. Operación restringida denegada a USER (403 Forbidden)**: Petición `DELETE /usuarios/1` con Basic Auth del usuario estándar `user@ucc.edu.ar`. Devuelve `403 Forbidden`.
   - **7. Operación restringida permitida a ADMIN (204 No Content)**: Petición `DELETE /reservas/1` con Basic Auth de `admin@ucc.edu.ar` / `Admin123!`. Devuelve `204 No Content`.
   - **8. Consulta por relación JPA (200 OK)**: Petición `GET /reservas/usuario/2`. Comprueba la relación `Reserva` $\to$ `Usuario` y corrobora que el usuario incluido en la respuesta tampoco expone contraseña ni hash.
   - **9. CRUD completo**: Pruebas de Crear, Listar, Buscar por ID, Modificar y Eliminar en las carpetas `4. CRUD Usuarios` y `5. CRUD Reservas`.

---

## 6. Ejecución del Proyecto y Tests Automatizados

### Requisitos:
- Java 21 JDK instalado.
- Maven (o utilizar el wrapper incluido `./mvnw` / `.\mvnw.cmd`).

### Compilar y Ejecutar:
```bash
# En Windows PowerShell o CMD:
.\mvnw.cmd spring-boot:run

# En Linux / macOS:
./mvnw spring-boot:run
```

### Ejecutar Tests Automatizados:
El proyecto cuenta con una suite completa de pruebas unitarias y de integración en Spring Boot Test / MockMvc:
```bash
.\mvnw.cmd test
```
Los tests verifican automáticamente los 9 escenarios de prueba de la rúbrica (códigos 201, 409, 400, 401, 200, 403, 204, BCrypt hash en DB y seguridad de DTOs).

---

## 7. Acceso a la Base de Datos H2

- **URL de la consola web**: `http://localhost:8080/h2-console`
- **JDBC URL**: `jdbc:h2:file:./data/reservas_db`
- **User Name**: `sa`
- **Password**: *(dejar en blanco)*

En la tabla `USUARIOS` se puede verificar visualmente que las contraseñas se encuentran protegidas mediante el hash BCrypt (`$2a$10$...`).
