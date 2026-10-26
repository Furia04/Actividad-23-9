package com.actividad.crud.exception;

public class CorreoYaExisteException extends RuntimeException {
    public CorreoYaExisteException(String correo) {
        super("El correo '" + correo + "' ya se encuentra registrado");
    }
}
