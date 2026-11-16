package com.novashop.novashop_backend.controller;

import com.novashop.novashop_backend.dto.UsuarioDto;
import com.novashop.novashop_backend.dto.UsuarioRequest;
import com.novashop.novashop_backend.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Administración de usuarios (solo admin)")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar usuarios")
    public List<UsuarioDto> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un usuario por id")
    public UsuarioDto obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    @Operation(summary = "Crear un usuario con rol admin o cliente")
    public ResponseEntity<UsuarioDto> crear(@Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un usuario (la clave es opcional)")
    public UsuarioDto actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequest request) {
        return service.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un usuario")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, Authentication auth) {
        service.eliminar(id, auth.getName());
        return ResponseEntity.noContent().build();
    }
}