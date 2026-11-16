package com.novashop.novashop_backend.dto;

public record AuthResponse(String accessToken, String refreshToken, String tipo, long expiraEnSegundos, UsuarioDto usuario) {
}