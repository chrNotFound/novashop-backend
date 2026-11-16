package com.novashop.novashop_backend.controller;

import com.novashop.novashop_backend.dto.PerfilRequest;
import com.novashop.novashop_backend.dto.UsuarioDto;
import com.novashop.novashop_backend.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/perfil")
@Tag(name = "Perfil", description = "Edición del propio perfil")
public class PerfilController {

    private final UsuarioService service;

    public PerfilController(UsuarioService service) {
        this.service = service;
    }

    @PutMapping
    @Operation(summary = "Actualizar nombre, apellidos y (opcional) contraseña del usuario autenticado")
    public UsuarioDto actualizar(@Valid @RequestBody PerfilRequest request, Authentication auth) {
        return service.actualizarPerfil(auth.getName(), request);
    }
}