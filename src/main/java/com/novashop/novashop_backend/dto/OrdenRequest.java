package com.novashop.novashop_backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record OrdenRequest(
        @NotNull(message = "Faltan los datos del cliente.") @Valid Cliente cliente,
        @NotNull(message = "Falta la dirección de entrega.") @Valid Direccion direccion,
        @NotEmpty(message = "El carrito está vacío.") List<@Valid Item> items,
        String resultadoPago) {

    public record Cliente(
            @NotBlank(message = "Ingresa tu nombre.") @Size(max = 80) String nombre,
            @NotBlank(message = "Ingresa tus apellidos.") @Size(max = 80) String apellidos,
            @NotBlank(message = "Ingresa tu correo.") @Email(message = "Correo no válido.") @Size(max = 120) String correo) {
    }

    public record Direccion(
            @NotBlank(message = "Ingresa la calle y el número.") @Size(max = 150) String calle,
            @Size(max = 30) String departamento,
            @NotBlank(message = "Selecciona una región.") @Size(max = 80) String region,
            @NotBlank(message = "Selecciona una comuna.") @Size(max = 80) String comuna,
            @Size(max = 300) String indicaciones) {
    }

    public record Item(
            @NotNull(message = "Falta el id del producto.") Long id,
            @NotNull(message = "Falta la cantidad.") @Min(value = 1, message = "La cantidad mínima es 1.") Integer cantidad) {
    }
}