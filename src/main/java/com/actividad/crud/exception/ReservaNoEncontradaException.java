package com.actividad.crud.exception;

public class ReservaNoEncontradaException extends RuntimeException {
    public ReservaNoEncontradaException(Long id) {
        super("Reserva no encontrada con ID: " + id);
    }

    public ReservaNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}
