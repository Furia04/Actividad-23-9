# Actividad 23-9 - CRUD de Usuarios y Reservas

Trabajo práctico de Laboratorio de Programación 2 (TUDS).
API REST desarrollada con Spring Boot para gestionar Usuarios y Reservas, con persistencia en base de datos relacional y manejo de excepciones propias.

## Requisitos
- Java 21
- Maven

## Entidades
- **Usuario**: id, nombre, correo, edad
- **Reserva**: id, fechaHora, cantidadPersonas, observaciones, usuarioId

## Endpoints

### Usuarios (`/usuarios`)
- `POST /usuarios`: Crear usuario
- `GET /usuarios`: Listar usuarios
- `GET /usuarios/{id}`: Buscar usuario por ID
- `PUT /usuarios/{id}`: Modificar usuario
- `DELETE /usuarios/{id}`: Eliminar usuario

### Reservas (`/reservas`)
- `POST /reservas`: Crear reserva
- `GET /reservas`: Listar reservas
- `GET /reservas/{id}`: Buscar reserva por ID
- `PUT /reservas/{id}`: Modificar reserva
- `DELETE /reservas/{id}`: Eliminar reserva

## Ejecución
Para correr el proyecto:
```bash
./mvnw spring-boot:run
```
O directamente ejecutando `CrudApplication.java` desde IntelliJ IDEA.

## Base de datos
Utiliza H2 en modo archivo almacenado en `./data/reservas_db`, manteniendo los datos guardados tras reiniciar la aplicación.

Para acceder a la consola web de H2:
- **URL**: `http://localhost:8080/h2-console`
- **JDBC URL**: `jdbc:h2:file:./data/reservas_db`
- **User Name**: `sa`
- **Password**: *(vacío)*

## Pruebas
Se incluye la colección `CRUD_Usuarios_Reservas.postman_collection.json` para importar en Postman y probar todas las operaciones del CRUD y las excepciones personalizadas.
