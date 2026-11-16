package com.novashop.novashop_backend.controller;

import com.novashop.novashop_backend.dto.CategoriaDto;
import com.novashop.novashop_backend.dto.CategoriaRequest;
import com.novashop.novashop_backend.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categorias")
@Tag(name = "Categorías", description = "CRUD de categorías (lectura pública, escritura solo admin)")
public class CategoriaController {

    private final CategoriaService service;

    public CategoriaController(CategoriaService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar categorías con su cantidad de productos")
    public List<CategoriaDto> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una categoría por id")
    public CategoriaDto obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    @Operation(summary = "Crear una categoría")
    public ResponseEntity<CategoriaDto> crear(@Valid @RequestBody CategoriaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una categoría")
    public CategoriaDto actualizar(@PathVariable Long id, @Valid @RequestBody CategoriaRequest request) {
        return service.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una categoría (solo si no tiene productos)")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}