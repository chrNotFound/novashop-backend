package com.novashop.novashop_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PerfilRequest(
        @NotBlank(message = "Ingresa tu nombre.") @Size(max = 80) String nombre,
        @NotBlank(message = "Ingresa tus apellidos.") @Size(max = 80) String apellidos,
        @Size(min = 6, max = 72, message = "La contraseña debe tener entre 6 y 72 caracteres.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "La contraseña debe combinar letras y números.")
        String clave) {
}