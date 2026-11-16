package com.novashop.novashop_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Ingresa tu correo.") @Email(message = "Correo no válido.") String correo,
        @NotBlank(message = "Ingresa tu contraseña.") String clave) {
}