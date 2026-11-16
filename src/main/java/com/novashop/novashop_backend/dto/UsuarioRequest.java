package com.novashop.novashop_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UsuarioRequest(
        @NotBlank(message = "Ingresa el nombre.") @Size(max = 80) String nombre,
        @NotBlank(message = "Ingresa los apellidos.") @Size(max = 80) String apellidos,
        @NotBlank(message = "Ingresa el correo.") @Email(message = "Correo no válido.") @Size(max = 120) String correo,
        @Size(min = 6, max = 72, message = "La contraseña debe tener entre 6 y 72 caracteres.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "La contraseña debe combinar letras y números.")
        String clave,
        @NotBlank(message = "Selecciona un rol.")
        @Pattern(regexp = "(?i)admin|cliente", message = "El rol debe ser admin o cliente.") String rol) {
}