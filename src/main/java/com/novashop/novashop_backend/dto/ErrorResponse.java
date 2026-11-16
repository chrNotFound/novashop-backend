package com.novashop.novashop_backend.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(LocalDateTime fecha, int estado, String error, String mensaje, Map<String, String> campos) {
    public static ErrorResponse de(int estado, String error, String mensaje) {
        return new ErrorResponse(LocalDateTime.now(), estado, error, mensaje, Map.of());
    }
}