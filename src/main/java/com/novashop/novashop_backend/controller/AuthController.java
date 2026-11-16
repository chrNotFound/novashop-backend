package com.novashop.novashop_backend.controller;

import com.novashop.novashop_backend.dto.AuthResponse;
import com.novashop.novashop_backend.dto.LoginRequest;
import com.novashop.novashop_backend.dto.RefreshRequest;
import com.novashop.novashop_backend.dto.RegistroRequest;
import com.novashop.novashop_backend.dto.UsuarioDto;
import com.novashop.novashop_backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Login, registro, renovación de token y usuario actual")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión y obtener tokens JWT")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return service.login(request);
    }

    @PostMapping("/register")
    @Operation(summary = "Crear una cuenta de cliente")
    public ResponseEntity<AuthResponse> registrar(@Valid @RequestBody RegistroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar el access token con un refresh token")
    public AuthResponse renovar(@Valid @RequestBody RefreshRequest request) {
        return service.renovar(request.refreshToken());
    }

    @GetMapping("/me")
    @Operation(summary = "Datos del usuario autenticado")
    public UsuarioDto yo(Authentication autenticacion) {
        return service.yo(autenticacion.getName());
    }
}