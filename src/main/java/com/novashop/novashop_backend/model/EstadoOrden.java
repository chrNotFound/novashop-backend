package com.novashop.novashop_backend.model;

import java.util.Arrays;

public enum EstadoOrden {
    PAGADA, ENVIADA, RECHAZADA, CANCELADA;

    public String valor() {
        return name().toLowerCase();
    }

    public boolean descuentaStock() {
        return this == PAGADA || this == ENVIADA;
    }

    public static EstadoOrden desde(String texto) {
        if (texto != null) {
            for (EstadoOrden e : values()) {
                if (e.name().equalsIgnoreCase(texto.trim())) {
                    return e;
                }
            }
        }
        throw new IllegalArgumentException("Estado no válido: " + texto + ". Valores permitidos: "
                + Arrays.stream(values()).map(EstadoOrden::valor).toList());
    }
}