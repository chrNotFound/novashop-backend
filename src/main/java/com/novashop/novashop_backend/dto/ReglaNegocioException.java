package com.novashop.novashop_backend.exception;

import org.springframework.http.HttpStatus;

public class ReglaNegocioException extends RuntimeException {
    private final HttpStatus estado;

    public ReglaNegocioException(String mensaje) {
        this(HttpStatus.CONFLICT, mensaje);
    }

    public ReglaNegocioException(HttpStatus estado, String mensaje) {
        super(mensaje);
        this.estado = estado;
    }

    public HttpStatus getEstado() {
        return estado;
    }
}