package com.novashop.novashop_backend.controller;

import com.novashop.novashop_backend.dto.ProductoDto;
import com.novashop.novashop_backend.dto.ProductoRequest;
import com.novashop.novashop_backend.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "CRUD de productos (lectura pública, escritura solo admin)")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar todos los productos")
    public List<ProductoDto> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un producto por id")
    public ProductoDto obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    @Operation(summary = "Crear un producto")
    public ResponseEntity<ProductoDto> crear(@Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un producto")
    public ProductoDto actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest request) {
        return service.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un producto")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}