package com.novashop.novashop_backend.service;

import com.novashop.novashop_backend.dto.CategoriaDto;
import com.novashop.novashop_backend.dto.CategoriaRequest;
import com.novashop.novashop_backend.exception.RecursoNoEncontradoException;
import com.novashop.novashop_backend.exception.ReglaNegocioException;
import com.novashop.novashop_backend.model.Categoria;
import com.novashop.novashop_backend.repository.CategoriaRepository;
import com.novashop.novashop_backend.repository.ProductoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoriaService {

    private final CategoriaRepository categorias;
    private final ProductoRepository productos;

    public CategoriaService(CategoriaRepository categorias, ProductoRepository productos) {
        this.categorias = categorias;
        this.productos = productos;
    }

    @Transactional(readOnly = true)
    public List<CategoriaDto> listar() {
        return categorias.findAll().stream()
                .map(c -> CategoriaDto.de(c, productos.countByCategoriaId(c.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoriaDto obtener(Long id) {
        Categoria c = buscar(id);
        return CategoriaDto.de(c, productos.countByCategoriaId(c.getId()));
    }

    @Transactional
    public CategoriaDto crear(CategoriaRequest request) {
        String nombre = request.nombre().trim();
        if (categorias.existsByNombreIgnoreCase(nombre)) {
            throw new ReglaNegocioException("Ya existe una categoría con ese nombre.");
        }
        Categoria c = new Categoria();
        c.setNombre(nombre);
        c = categorias.save(c);
        return CategoriaDto.de(c, 0);
    }

    @Transactional
    public CategoriaDto actualizar(Long id, CategoriaRequest request) {
        Categoria c = buscar(id);
        String nombre = request.nombre().trim();
        categorias.findByNombreIgnoreCase(nombre).ifPresent(otra -> {
            if (!otra.getId().equals(id)) {
                throw new ReglaNegocioException("Ya existe una categoría con ese nombre.");
            }
        });
        c.setNombre(nombre);
        c = categorias.save(c);
        return CategoriaDto.de(c, productos.countByCategoriaId(c.getId()));
    }

    @Transactional
    public void eliminar(Long id) {
        Categoria c = buscar(id);
        if (productos.countByCategoriaId(id) > 0) {
            throw new ReglaNegocioException("No se puede eliminar una categoría que tiene productos.");
        }
        categorias.delete(c);
    }

    private Categoria buscar(Long id) {
        return categorias.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada: " + id));
    }
}