package com.actividad.crud.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsuarioNoEncontradoException.class)
    public ResponseEntity<ErrorDetalle> manejarUsuarioNoEncontrado(UsuarioNoEncontradoException ex) {
        ErrorDetalle error = new ErrorDetalle(
                HttpStatus.NOT_FOUND.value(),
                "Usuario no encontrado",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ReservaNoEncontradaException.class)
    public ResponseEntity<ErrorDetalle> manejarReservaNoEncontrada(ReservaNoEncontradaException ex) {
        ErrorDetalle error = new ErrorDetalle(
                HttpStatus.NOT_FOUND.value(),
                "Reserva no encontrada",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidacionException.class)
    public ResponseEntity<ErrorDetalle> manejarValidacion(ValidacionException ex) {
        ErrorDetalle error = new ErrorDetalle(
                HttpStatus.BAD_REQUEST.value(),
                "Error de validación de negocio",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetalle> manejarValidacionCampos(MethodArgumentNotValidException ex) {
        List<String> errores = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.toList());

        ErrorDetalle error = new ErrorDetalle(
                HttpStatus.BAD_REQUEST.value(),
                "Parámetros inválidos",
                "Uno o más campos presentan errores de validación",
                errores
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorDetalle> manejarRecursoNoEncontrado(NoResourceFoundException ex) {
        ErrorDetalle error = new ErrorDetalle(
                HttpStatus.NOT_FOUND.value(),
                "Recurso no encontrado",
                "La ruta solicitada no existe: /" + ex.getResourcePath()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetalle> manejarErrorGeneral(Exception ex) {
        ErrorDetalle error = new ErrorDetalle(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Error interno del servidor",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
