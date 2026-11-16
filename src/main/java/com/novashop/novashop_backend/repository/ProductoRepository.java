package com.novashop.novashop_backend.repository;

import com.novashop.novashop_backend.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    long countByCategoriaId(Long categoriaId);
}