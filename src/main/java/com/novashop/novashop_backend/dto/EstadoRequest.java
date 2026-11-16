package com.novashop.novashop_backend.dto;

import jakarta.validation.constraints.NotBlank;

public record EstadoRequest(@NotBlank(message = "Indica el estado.") String estado) {
}