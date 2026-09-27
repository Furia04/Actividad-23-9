package com.actividad.crud.exception;

public class UsuarioNoEncontradoException extends RuntimeException {
    public UsuarioNoEncontradoException(Long id) {
        super("Usuario no encontrado con ID: " + id);
    }

    public UsuarioNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
