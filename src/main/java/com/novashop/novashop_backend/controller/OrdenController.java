package com.novashop.novashop_backend.controller;

import com.novashop.novashop_backend.dto.EstadoRequest;
import com.novashop.novashop_backend.dto.OrdenDto;
import com.novashop.novashop_backend.dto.OrdenRequest;
import com.novashop.novashop_backend.service.OrdenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ordenes")
@Tag(name = "Órdenes", description = "Compras: crear (usuario autenticado), consultar y cambiar estado (admin)")
public class OrdenController {

    private final OrdenService service;

    public OrdenController(OrdenService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear una compra (recalcula precios y descuenta stock)")
    public ResponseEntity<OrdenDto> crear(@Valid @RequestBody OrdenRequest request, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request, auth.getName()));
    }

    @GetMapping
    @Operation(summary = "Listar todas las órdenes (solo admin). Filtro opcional por usuarioId")
    public List<OrdenDto> listar(@RequestParam(required = false) Long usuarioId) {
        return service.listar(usuarioId);
    }

    @GetMapping("/mias")
    @Operation(summary = "Listar las órdenes del usuario autenticado")
    public List<OrdenDto> mias(Authentication auth) {
        return service.listarMias(auth.getName());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una orden (el dueño o un admin)")
    public OrdenDto obtener(@PathVariable Long id, Authentication auth) {
        boolean esAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return service.obtener(id, auth.getName(), esAdmin);
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar el estado de una orden (solo admin)")
    public OrdenDto cambiarEstado(@PathVariable Long id, @Valid @RequestBody EstadoRequest request) {
        return service.cambiarEstado(id, request.estado());
    }
}