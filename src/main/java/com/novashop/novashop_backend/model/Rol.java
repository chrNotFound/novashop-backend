package com.novashop.novashop_backend.model;

public enum Rol {
    ADMIN,
    CLIENTE;

    public String valor() {
        return name().toLowerCase();
    }

    public static Rol desde(String texto) {
        if (texto == null) {
            return CLIENTE;
        }
        return switch (texto.trim().toLowerCase()) {
            case "admin" -> ADMIN;
            case "cliente" -> CLIENTE;
            default -> throw new IllegalArgumentException("Rol no válido: " + texto);
        };
    }
}