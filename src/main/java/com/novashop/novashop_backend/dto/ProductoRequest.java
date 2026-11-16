package com.novashop.novashop_backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProductoRequest(
        @NotBlank(message = "Ingresa el nombre del producto.") @Size(max = 150) String nombre,
        @NotNull(message = "Ingresa el precio.") @Min(value = 1, message = "El precio debe ser mayor a 0.") Integer precio,
        @NotNull(message = "Ingresa el stock.") @Min(value = 0, message = "El stock no puede ser negativo.") Integer stock,
        @Size(max = 120) String atributo,
        @NotBlank(message = "Ingresa la categoría.") @Size(max = 60) String categoria,
        @Min(value = 0, message = "El descuento mínimo es 0.") @Max(value = 100, message = "El descuento máximo es 100.")
        Integer descuento,
        @NotBlank(message = "Ingresa la ruta de la imagen.") @Size(max = 300)
        @Pattern(regexp = "^(/|https?://).*", message = "La ruta debe empezar con / o con https://.") String imagen,
        @NotBlank(message = "Ingresa la descripción del producto.") @Size(max = 1000) String descripcion) {
}