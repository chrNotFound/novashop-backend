package com.novashop.novashop_backend.service;

import com.novashop.novashop_backend.dto.ProductoDto;
import com.novashop.novashop_backend.dto.ProductoRequest;
import com.novashop.novashop_backend.exception.RecursoNoEncontradoException;
import com.novashop.novashop_backend.model.Categoria;
import com.novashop.novashop_backend.model.Producto;
import com.novashop.novashop_backend.repository.CategoriaRepository;
import com.novashop.novashop_backend.repository.ProductoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductoService {

    private final ProductoRepository productos;
    private final CategoriaRepository categorias;

    public ProductoService(ProductoRepository productos, CategoriaRepository categorias) {
        this.productos = productos;
        this.categorias = categorias;
    }

    @Transactional(readOnly = true)
    public List<ProductoDto> listar() {
        return productos.findAll().stream().map(ProductoDto::de).toList();
    }

    @Transactional(readOnly = true)
    public ProductoDto obtener(Long id) {
        return ProductoDto.de(buscar(id));
    }

    @Transactional
    public ProductoDto crear(ProductoRequest request) {
        Producto p = new Producto();
        aplicar(p, request);
        return ProductoDto.de(productos.save(p));
    }

    @Transactional
    public ProductoDto actualizar(Long id, ProductoRequest request) {
        Producto p = buscar(id);
        aplicar(p, request);
        return ProductoDto.de(productos.save(p));
    }

    @Transactional
    public void eliminar(Long id) {
        productos.delete(buscar(id));
    }

    private Producto buscar(Long id) {
        return productos.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado: " + id));
    }

    private void aplicar(Producto p, ProductoRequest r) {
        p.setNombre(r.nombre().trim());
        p.setPrecio(r.precio());
        p.setStock(r.stock());
        p.setAtributo(r.atributo() == null ? "" : r.atributo().trim());
        p.setDescuento(r.descuento() == null ? 0 : r.descuento());
        p.setImagen(r.imagen().trim());
        p.setDescripcion(r.descripcion().trim());
        p.setCategoria(resolverCategoria(r.categoria()));
    }

    private Categoria resolverCategoria(String nombre) {
        String limpio = nombre.trim();
        return categorias.findByNombreIgnoreCase(limpio).orElseGet(() -> {
            Categoria nueva = new Categoria();
            nueva.setNombre(limpio);
            return categorias.save(nueva);
        });
    }
}